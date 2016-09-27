package nl.isaac.dotcms.searcher.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.isaac.dotcms.searcher.shared.SearchMode;

public class TextUtil {

	private final SearchMode searchMode;
	private final int snippetSizeBefore;
	private final int snippetSizeAfter;
	private final String excludeText;

	public TextUtil(String searchMode, int snippetSizeBefore, int snippetSizeAfter, String excludeText) {
		super();
		this.searchMode = SearchMode.valueOf(searchMode.toUpperCase());
		this.snippetSizeBefore = snippetSizeBefore;
		this.snippetSizeAfter = snippetSizeAfter;
		this.excludeText = excludeText;
	}

	/**
	 * Get snippets by text or regex
	 * 
	 * @param text
	 * @param valueToSearchFor
	 *            a text or regex value
	 * @return List of snippets if found else null
	 */
	public List<String> getSnippetFromText(String text, String valueToSearchFor) {
		if (text != null && valueToSearchFor != null) {
			List<String> snippets = new ArrayList<String>();
			String lowerCaseText = text.toLowerCase();

			if (this.searchMode == SearchMode.TEXT) {
				String lowerCaseWordToSearchFor = valueToSearchFor.toLowerCase();

				int textIndex = 0;

				while ((textIndex = lowerCaseText.indexOf(lowerCaseWordToSearchFor)) > -1) {
					String snippet = text.substring(Math.max(0, textIndex - this.snippetSizeBefore),
							Math.min(textIndex + valueToSearchFor.length() + this.snippetSizeAfter, text.length()));

					if (this.excludeText == null || !snippet.toLowerCase().contains(this.excludeText.toLowerCase())) {
						snippets.add(snippet);
					}
					text = text.substring(Math.max(0, textIndex + valueToSearchFor.length()));
					lowerCaseText = lowerCaseText.substring(Math.max(0, textIndex + valueToSearchFor.length()));
				}
			} else if (this.searchMode == SearchMode.REGEX) {
				Pattern pattern = Pattern.compile(valueToSearchFor);
				Matcher matcher = pattern.matcher(text);

				int textIndex = 0;

				while (matcher.find()) {
					if ((textIndex = text.indexOf(matcher.group())) > -1 && matcher.start() >= 0) {
						String snippet = text.substring(Math.max(0, textIndex - this.snippetSizeBefore),
								Math.min(textIndex + matcher.group().length() + this.snippetSizeAfter, text.length()));

						if (this.excludeText == null
								|| !snippet.toLowerCase().contains(this.excludeText.toLowerCase())) {
							snippets.add(snippet);
						}

						text = text.substring(Math.max(0, textIndex + matcher.group().length()));
					}
				}
			}

			if (snippets.size() > 0) {
				return snippets;
			}
		}
		return null;
	}

	public boolean checkValueForText(String value, String text) {
		if (value == null)
			return false;

		if (this.searchMode == SearchMode.TEXT) {
			if (this.excludeText != null) {
				return value.toLowerCase().contains(text.toLowerCase())
						&& !value.toLowerCase().contains(this.excludeText.toLowerCase());
			} else {
				return value.toLowerCase().contains(text.toLowerCase());
			}
		}
		// Regex comparison
		else if (this.searchMode == SearchMode.REGEX) {
			Pattern pattern = Pattern.compile(text);
			Matcher matcher = pattern.matcher(value);

			if (this.excludeText != null) {
				return matcher.find() && !value.toLowerCase().contains(this.excludeText.toLowerCase());
			} else {
				return matcher.find();
			}
		}
		return false;
	}

}
