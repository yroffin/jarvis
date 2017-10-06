package org.jarvis.main.model.impl.transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jarvis.main.engine.transform.IAimlScore;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.transform.IDebugTable;
import org.jarvis.main.model.transform.ITransformedItem;

/**
 * DebugTableImpl
 */
public class DebugTableImpl implements IDebugTable {

	/**
	 * instance
	 */
	public static IDebugTable instance = new DebugTableImpl();

	private List<List<IAimlCategory>> listTableCats = new ArrayList<List<IAimlCategory>>();
	private List<List<tableRow>> listTableRows = new ArrayList<List<tableRow>>();
	private List<ITransformedItem> listSentence = new ArrayList<ITransformedItem>();
	private List<String> listTopic = new ArrayList<String>();

	private class tableRow {

		private String sessiondIdText;
		private String catXmlValue;
		private int score;
		private ITransformedItem toScore;
		private IAimlScore found;
		private IAimlScore old;

		public tableRow(String sessiondIdText, String catXmlValue, int score,
				ITransformedItem toScore, IAimlScore found, IAimlScore old) {
			this.sessiondIdText = sessiondIdText;
			this.catXmlValue = catXmlValue;
			this.score = score;
			this.toScore = toScore;
			this.found = found;
			this.old = old;
		}

		public String toTableRow(int line) {
			return "<tr><td>" + line + "</td><td>" + sessiondIdText
					+ "</td><td>" + StringEscapeUtils.escapeHtml4(catXmlValue)
					+ "</td><td>" + score + "</td><td>" + toScore + "</td><td>"
					+ found + "</td><td>" + old + "</td></tr>";
		}

	}

	@Override
	public void addCategories(List<IAimlCategory> availableCategories,
			ITransformedItem sentence, String topic) {
		listTableCats.add(availableCategories);
		listSentence.add(sentence);
		listTopic.add(topic);
		listTableRows.add(new ArrayList<tableRow>());
	}

	@Override
	public void addFound(String sessiondIdText, String catXmlValue, int score,
			ITransformedItem toScore, IAimlScore found, IAimlScore old) {
		List<tableRow> list = listTableRows.get(listTableRows.size() - 1);
		tableRow elt = new tableRow(sessiondIdText, catXmlValue, score,
				toScore, found, old);
		list.add(elt);
	}

	@Override
	public File store() throws IOException {
		File html = File.createTempFile("debug", ".html");
		FileWriter fw = new FileWriter(html);
		fw.write("<html>\r\n");
		fw.write("<head>\r\n");
		fw.write("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css\">\r\n");
		fw.write("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css\">\r\n");
		fw.write("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js\"></script>");
		fw.write("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js\"></script>\r\n");
		fw.write("</head>\r\n");
		fw.write("<body>\r\n");

		int cat = 0;
		fw.write("<div role=\"tabpanel\">");
		fw.write("<ul class=\"nav nav-tabs\" role=\"tablist\">");
		for (List<IAimlCategory> categories : listTableCats) {
			String active = "";
			if (cat == 0) {
				active = "class=\"active\"";
			}
			fw.write("<li role=\"presentation\" " + active
					+ "><a href=\"#tab-cat-" + cat
					+ "\" aria-controls=\"tab-cat-" + cat
					+ "\" role=\"tab\" data-toggle=\"tab\">Categorie " + cat
					+ "</a></li>");
			fw.write("<li role=\"presentation\"><a href=\"#tab-list-" + cat
					+ "\" aria-controls=\"tab-list-" + cat
					+ "\" role=\"tab\" data-toggle=\"tab\">List " + cat
					+ "</a></li>");
			cat++;
		}
		fw.write("</ul>");

		cat = 0;
		fw.write("<div class=\"tab-content\">");
		for (List<IAimlCategory> categories : listTableCats) {
			String active = "";
			if (cat == 0) {
				active = "active";
			}
			fw.write("<div role=\"tabpanel\" class=\"tab-pane " + active
					+ "\" id=\"tab-cat-" + cat + "\">");
			fw.write("<h1>Category " + cat + "/" + listSentence.get(cat) + "/"
					+ listTopic.get(cat) + "</h1>\r\n");
			fw.write("<table class=\"table table-striped\">\r\n");
			fw.write("<thead>\r\n");
			fw.write("<th>#</th>\r\n");
			fw.write("<th>Categorie</th>\r\n");
			fw.write("</thead>\r\n");
			fw.write("<tbody>\r\n");
			int line = 0;
			for (IAimlCategory category : categories) {
				fw.write("<tr><td>" + (line++) + "</td><td>"
						+ category.escapedHtml() + "</td></tr>" + "\r\n");
			}
			fw.write("</tbody>\r\n");
			fw.write("</table>\r\n");
			fw.write("</div>");

			fw.write("<div role=\"tabpanel\" class=\"tab-pane\" id=\"tab-list-"
					+ cat + "\">");
			List<tableRow> list = listTableRows.get(cat);
			fw.write("<h1>List " + cat + "</h1>\r\n");
			fw.write("<table class=\"table table-striped\">\r\n");
			fw.write("<thead>\r\n");
			fw.write("<th>#</th>\r\n");
			fw.write("<th>Session</th>\r\n");
			fw.write("<th>Cat/Xml</th>\r\n");
			fw.write("<th>Score</th>\r\n");
			fw.write("<th>To score</th>\r\n");
			fw.write("<th>Found</th>\r\n");
			fw.write("<th>Old</th>\r\n");
			fw.write("</thead>\r\n");
			fw.write("<tbody>\r\n");
			int row = 0;
			for (tableRow tableRow : list) {
				fw.write(tableRow.toTableRow(row++) + "\r\n");
			}
			fw.write("</tbody>\r\n");
			fw.write("</table>\r\n");
			fw.write("</div>");
			cat++;
		}
		fw.write("</div>");
		fw.write("</div>");
		fw.write("<script>$(function () {$('#myTab a:last').tab('show')})</script>\r\n");
		fw.write("</body>\r\n");
		fw.close();
		return html;
	}
}
