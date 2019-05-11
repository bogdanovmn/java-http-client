package com.github.bogdanovmn.httpclient.translate.google;

import com.github.bogdanovmn.httpclient.phantomjs.SeleniumPhantomJsHttpClient;
import com.github.bogdanovmn.httpclient.translate.HttpTranslateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class GoogleTranslate extends HttpTranslateService {

	public GoogleTranslate() {
		super(
			new SeleniumPhantomJsHttpClient(),
			"https://translate.google.ru/?ie=UTF-8#en/ru/"
		);
	}

	@Override
	protected Set<String> parsedServiceResponse(String htmlText, String phrase) {
		Document doc = Jsoup.parse(htmlText);
		Element resultBox = doc
			.select("span[class=tlid-translation translation]")
			.first();

		Set<String> result = null;
		if (resultBox != null) {
			result = new HashSet<String>() {{ add(resultBox.text()); }};
			Elements rows = doc.select("table[class=gt-baf-table] tr");
			if (rows != null) {
				int currentGroupCount = 0;
				int groupLimit = 2;
				for (Element row : rows) {
					Element head = row.select("span[class=gt-cd-pos]").first();
					if (head != null) {
						currentGroupCount = 0;
						if (result.size() > 1) {
							groupLimit = 1;
						}
						System.out.println(head.text());
					}
					else {
//						Element translate = row.select("span[class=gt-baf-word-clickable]").first();
						Element translate = row.select("span[class=gt-baf-cell gt-baf-word-clickable]").first();
						if (translate != null) {
							if (currentGroupCount < groupLimit) {
								System.out.println("\t" + translate.text());
								result.add(translate.text().toLowerCase());
								currentGroupCount++;
							}
						}
					}
				}
			}
		}

		return result;
	}
}
