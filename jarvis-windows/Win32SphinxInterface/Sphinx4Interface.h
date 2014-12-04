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

#pragma once

#include <Windows.h>
#include <Windows.h>

#include <stdio.h>
#include <string.h>

#include <err.h>
#include <ad.h>
#include <cont_ad.h>
#include <cmd_ln.h>

#include <pocketsphinx.h>

#define CONSOLE_STR(text0, text1, text2) printConsoleMessage(__FILE__, __LINE__, "INFO", text0, text1, text2)
#define CONSOLE_INFO(text0, text1) printConsoleMessage(__FILE__, __LINE__, "INFO", text0, text1)
#define CONSOLE_WARN(text)	  printConsoleMessage(__FILE__, __LINE__, "WARN", "%s", text)
#define CONSOLE_ERROR(text)	  printConsoleMessage(__FILE__, __LINE__, "ERROR", "%s", text)

using namespace System;
using namespace System::Runtime::InteropServices;

// char to String
#define CONVERT2STRING(x, len) Marshal::PtrToStringAnsi(IntPtr((void*) x ), strlen(x))

// creation
#define CONVERT2CHAR(x) (char*)(Marshal::StringToHGlobalAnsi(x).ToPointer())

// libération
#define FREECHAR(x) Marshal::FreeHGlobal(IntPtr((void*) x ));


namespace WindowsJarvisClient {

	public ref class Sphinx4InterfaceSentence
	{
		public:
			String^ uttid;
			String^ hyp;
	};

	public ref class Sphinx4Interface
	{
		public:
			delegate void callback(const char*message);
			delegate void callbackUtterance(const char *uttId, const char *value);
			Sphinx4Interface();
			int init(String^ models, String^ language, String^ dictionnary);
			int openMicrophone();
			int readFromMicrophone(Sphinx4InterfaceSentence^ sentence);
			int closeMicrophone();
			int liberation();
			bool active = true;
			virtual ~Sphinx4Interface();
		private:
			void printConsoleMessage(char const *f, long ln, char const *msg, char const *text0, char const *text1);
			void printConsoleMessage(char const *f, long ln, char const *msg, char const *text0, char const *text1, char const *text2);
			cmd_ln_t *config;
			ps_decoder_t *ps;
			cont_ad_t *cont;
			ad_rec_t *ad;
	};

}