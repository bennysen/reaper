import org.cabbage.crawler.reaper.commons.filter.ULRStringFilterFactory;
import org.cabbage.crawler.reaper.commons.filter.URLStringFilter;

public class Test {

	public static void main(String[] args) {
		URLStringFilter usf = ULRStringFilterFactory.getInstance().getFilter("qq.com", URLStringFilter.OUTPUT);
		for (String s : usf.getFilters()) {
			System.out.println(s);
		}
	}

}
