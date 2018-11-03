package org.cabbage.crawler.reaper.worker.utils;

import java.io.File;
import java.io.IOError;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.file.FileUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * 本地去重工具
 * 
 * @author wkshen
 *
 */
public class ReduceUtils {

	private static final Log LOGGER = LogFactory.getLog(ReduceUtils.class);

	private static DB MAP_DB = null;
	/**
	 * <domain,MergeBean>
	 */
	private static ConcurrentNavigableMap<String, ReduceBean> MAP = null;

	public static void initMapDB() {
		try {
			MAP_DB = DBMaker.newFileDB(new File("reducerDB")).closeOnJvmShutdown().make();
			MAP = MAP_DB.getTreeMap("reducerMap");
		} catch (Exception e) {
			LOGGER.error("Load mapdb error!", e);
			rebuildMapDB();
		} catch (IOError e) {
			LOGGER.error("Load mapdb error!", e);
			rebuildMapDB();
		}
	}

	public synchronized static Set<String> reduce(String domain, Set<String> urls) {
		if (null == domain || domain.trim().length() == 0 || null == urls || urls.size() == 0) {
			return null;
		}

		ReduceBean persistent = MAP.get(domain);
		if (null == persistent) {
			persistent = new ReduceBean();
			persistent.setKey(domain);
			persistent.setOutLinks(new HashSet<String>());
		}
		ReduceBean current = new ReduceBean();
		current.setKey(domain);
		current.setOutLinks(urls);

		Set<String> dist = reduce(urls, persistent.getOutLinks());
		persistent.getOutLinks().addAll(urls);
		MAP.put(domain, persistent);
		MAP_DB.commit();
		return dist;

	}

	private static Set<String> reduce(Set<String> current, Set<String> last) {
		Set<String> c = new HashSet<String>();
		c.addAll(current);
		c.removeAll(last);
		return c;
	}

	private static void rebuildMapDB() {
		try {
			List<File> fs = FileUtils.listFiles(new File("."));
			if (null != fs && fs.size() > 0) {
				for (File f : fs) {
					if (null != f && f.getName().contains("reducerDB")) {
						f.delete();
					}
				}
			}
			fs = FileUtils.listFiles(new File("."));
			if (null != fs && fs.size() > 0) {
				for (File f : fs) {
					if (null != f && f.getName().contains("backupDB")) {
						f.renameTo(new File(f.getName().replace("backupDB", "reducerDB")));
					}
				}
			}
			MAP_DB = DBMaker.newFileDB(new File("reducerDB")).closeOnJvmShutdown().make();
			MAP = MAP_DB.getTreeMap("reducerMap");
		} catch (Exception e1) {
			LOGGER.error("Rebuild mapdb error!System exit!", e1);
			System.exit(-1);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
