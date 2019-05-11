package com.github.bogdanovmn.httpclient.translate.google;

import com.github.bogdanovmn.httpclient.translate.google.GoogleTranslate;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoogleTranslateTest {
	@Test
	public void parseServiceRawAnswer() throws Exception {
		String html = new String(
			Files.readAllBytes(
				Paths.get(
					getClass().getResource("/translate--bar--html").toURI()
				)
			),
			"UTF8"
		);

		Set<String> translates = new GoogleTranslate().parsedServiceResponse(html, "bar");

		assertEquals(
			"Translates count", 5, translates.size()
		);

		assertTrue(
			"All translates",
			translates.equals(
				Sets.newHashSet("бар", "полоса", "запирать", "брусковый", "не считая")
			)
		);
	}
}