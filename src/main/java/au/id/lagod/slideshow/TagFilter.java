package au.id.lagod.slideshow;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagFilter {
	
	final static Logger logger = LoggerFactory.getLogger(Image.class);

	private List<String> excludedTagsList;
	private List<String> includedTagsList;

	public TagFilter(String excludedTags, String includedTags) {
		excludedTagsList = Arrays.asList(excludedTags.split("\\s*,\\s*"));
		includedTagsList = Arrays.asList(includedTags.split("\\s*,\\s*"));
	}

	public boolean showTag(String tag) {
		return include(tag) || !exclude(tag);
	}

	private boolean exclude(String tag) {
		for (String excTag: excludedTagsList) {
			if (tag.matches(excTag)) {
				return true;
			}
		}
		return false;
	}

	private boolean include(String tag) {
		if (tag.equals("")) return false;
		
		for (String incTag: includedTagsList) {
			logger.debug(" matching " + incTag + " tag " + tag);
			if (tag.matches(incTag)) {
				logger.debug("include tag YES");
				return true;
			}
		}
		return false;
	}

}
