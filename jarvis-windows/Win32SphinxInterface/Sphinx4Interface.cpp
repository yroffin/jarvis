/**
* Copyright 2014 Yannick Roffin.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

#include "Sphinx4Interface.h"

namespace WindowsJarvisClient {
	/**
	* Constructor
	*/
	Sphinx4Interface::Sphinx4Interface()
	{
	}

	/**
	 * init
	 */
	int Sphinx4Interface::init(String^ models, String^ language, String^ dictionnary)
	{
		/**
		* internal structure
		*/
		static const arg_t cont_args_def[] = {
			POCKETSPHINX_OPTIONS,
			/* Argument file. */
			{ "-argfile", ARG_STRING, NULL, "Argument file giving extra arguments." },
			{ "-adcdev", ARG_STRING, NULL, "Name of audio device to use for input." },
			{ "-infile", ARG_STRING, NULL, "Audio file to transcribe." },
			{ "-time", ARG_BOOLEAN, "no", "Print word times in file transcription." },
			CMDLN_EMPTY_OPTION
		};

		const char *_models = CONVERT2CHAR(models);
		const char *_language = CONVERT2CHAR(language);
		const char *_dictionnary = CONVERT2CHAR(dictionnary);

		const char *params[7];
		params[0] = "binaire";
		params[1] = "-hmm";
		params[2] = _models;
		params[3] = "-lm";
		params[4] = _language;
		params[5] = "-dict";
		params[6] = _dictionnary;

		config = cmd_ln_parse_r(NULL, cont_args_def, 7, (char **)params, FALSE);
		char const *cfg;

		/* Handle argument file as -argfile. */
		if (config && (cfg = cmd_ln_str_r(config, "-argfile")) != NULL) {
			config = cmd_ln_parse_file_r(config, cont_args_def, cfg, FALSE);
		}
		if (config == NULL)
			return 1;

		CONSOLE_STR("Models: ", _models, "\n");
		CONSOLE_STR("Language: ", _language, "\n");
		CONSOLE_STR("Dictionnary: ", _dictionnary, "\n");

		ps = ps_init(config);
		if (ps == NULL)
			return 1;
	}

	/**
	* Main treatment
	*/
	int Sphinx4Interface::openMicrophone()
	{
		if ((ad = ad_open_dev(cmd_ln_str_r(config, "-adcdev"),
			(int)cmd_ln_float32_r(config, "-samprate"))) == NULL)
			CONSOLE_ERROR("Failed to open audio device\n");

		/* Initialize continuous listening module */
		CONSOLE_INFO("Initialize voice activity detection", "\n");
		if ((cont = cont_ad_init(ad, ad_read)) == NULL)
			CONSOLE_ERROR("Failed to initialize voice activity detection\n");
		CONSOLE_INFO("Start recording", "\n");
		if (ad_start_rec(ad) < 0)
			CONSOLE_ERROR("Failed to start recording\n");
		CONSOLE_INFO("Calibrate voice activity detection", "\n");
		if (cont_ad_calib(cont) < 0)
			CONSOLE_ERROR("Failed to calibrate voice activity detection\n");

		return 0;
	}

	/**
	* Main treatment
	*/
	int Sphinx4Interface::readFromMicrophone(Sphinx4InterfaceSentence^ sentence)
	{
		int32 ts, rem;
		int32 k;
		int16 adbuf[4096];
		char const *hyp;
		char const *uttid;
		char word[256];

		/* Indicate listening for next utterance */
		CONSOLE_INFO("READY....", "\n");

		/* Wait data for next utterance */
		while ((k = cont_ad_read(cont, adbuf, 4096)) == 0)
			Sleep(100);

		CONSOLE_INFO("Read audio", "\n");
		if (k < 0)
			CONSOLE_ERROR("Failed to read audio\n");

		/*
		* Non-zero amount of data received; start recognition of new utterance.
		* NULL argument to uttproc_begin_utt => automatic generation of utterance-id.
		*/
		CONSOLE_INFO("Start utterance", "\n");
		if (ps_start_utt(ps, NULL) < 0)
			CONSOLE_ERROR("Failed to start utterance\n");
		ps_process_raw(ps, adbuf, k, FALSE, FALSE);
		CONSOLE_INFO("Listening ...", "\n");

		/* Note timestamp for this first block of data */
		ts = cont->read_ts;

		/* Decode utterance until end (marked by a "long" silence, >1sec) */
		for (;;) {
			/* Read non-silence audio data, if any, from continuous listening module */
			if ((k = cont_ad_read(cont, adbuf, 4096)) < 0)
				CONSOLE_ERROR("Failed to read audio\n");
			if (k == 0) {
				/*
				* No speech data available; check current timestamp with most recent
				* speech to see if more than 1 sec elapsed.  If so, end of utterance.
				*/
				if ((cont->read_ts - ts) > DEFAULT_SAMPLES_PER_SEC)
					break;
			}
			else {
				/* New speech data received; note current timestamp */
				ts = cont->read_ts;
			}

			/*
			* Decode whatever data was read above.
			*/
			rem = ps_process_raw(ps, adbuf, k, FALSE, FALSE);

			/* If no work to be done, sleep a bit */
			if ((rem == 0) && (k == 0))
				Sleep(20);
		}

		/*
		* Utterance ended; flush any accumulated, unprocessed A/D data and stop
		* listening until current utterance completely decoded
		*/
		ad_stop_rec(ad);
		while (ad_read(ad, adbuf, 4096) >= 0);
		cont_ad_reset(cont);

		CONSOLE_INFO("Stopped listening, please wait...", "\n");
		/* Finish decoding, obtain and print result */
		ps_end_utt(ps);
		hyp = ps_get_hyp(ps, NULL, &uttid);
		CONSOLE_INFO("Uid:", uttid);
		CONSOLE_INFO("Hyp:", hyp);
		sentence->uttid = CONVERT2STRING(uttid);
		sentence->hyp = CONVERT2STRING(hyp);

		/* Resume A/D recording for next utterance */
		CONSOLE_INFO("Start recording", "\n");
		if (ad_start_rec(ad) < 0)
			CONSOLE_ERROR("Failed to start recording\n");

		return 0;
	}

	/**
	* Main treatment
	*/
	int Sphinx4Interface::closeMicrophone()
	{
		cont_ad_close(cont);
		ad_close(ad);
		return 0;
	}

	/**
	 * liberation
	 */
	int Sphinx4Interface::liberation()
	{
		ps_free(ps);
		return 0;
	}

	/**
	* simple log handler
	*/
	void Sphinx4Interface::printConsoleMessage(char const *f, long ln, char const *msg, char const *text0, char const *text1) {
		char back[1024];

		char const *fname;
		fname = strrchr(f, '\\');
		if (fname == NULL) fname = strrchr(f, '/');
		sprintf_s(back, "%s: %s(%ld): %s %s", msg, fname == NULL ? f : fname + 1, ln, text0, text1);
		if (back[strlen(back) - 1] == '\n') back[strlen(back) - 1] = 0;
		Console::WriteLine(CONVERT2STRING(back));
	}

	/**
	* simple log handler
	*/
	void Sphinx4Interface::printConsoleMessage(char const *f, long ln, char const *msg, char const *text0, char const *text1, char const *text2) {
		char back[1024];

		char const *fname;
		fname = strrchr(f, '\\');
		if (fname == NULL) fname = strrchr(f, '/');
		sprintf_s(back, "%s: %s(%ld): %s %s %s", msg, fname == NULL ? f : fname + 1, ln, text0, text1, text2);
		if(back[strlen(back)-1] == '\n') back[strlen(back) - 1] = 0;
		Console::WriteLine(CONVERT2STRING(back));
	}

	Sphinx4Interface::~Sphinx4Interface()
	{
	}
}