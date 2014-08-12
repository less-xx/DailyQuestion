/**
 * 
 */
package org.teapotech.dc.question;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jiangl
 *
 */
@Component
public class QuestionTypeChangeDetector extends Thread {
	private static Logger logger = LoggerFactory
	        .getLogger(QuestionTypeChangeDetector.class);
	private static File homeDir = new File(System.getProperty(
	        "QUESTION_TYPE_HOME", "question_types"));
	private WatchService watcher;
	private HashMap<WatchKey, Path> registeredPaths = new HashMap<WatchKey, Path>();
	private LinkedList<QuestionTypeChangeListener> listeners = new LinkedList<QuestionTypeChangeListener>();
	private HashSet<String> questionTypeNames = new HashSet<String>();
	private boolean running;
	private String ignoreFileTypes = "js,css,html,htm,txt,jpeg,jpg,png,gif";
	private final TreeSet<String> ignoreFileTypeSet = new TreeSet<String>();

	public String getIgnoreFileTypes() {
		return ignoreFileTypes;
	}

	public void setIgnoreFileTypes(String ignoreFileTypes) {
		this.ignoreFileTypes = ignoreFileTypes;
	}

	@PostConstruct
	protected void init() throws Exception {
		if (ignoreFileTypes != null) {
			String[] ss = ignoreFileTypes.split("\\s*,\\s*");
			for (String s : ss) {
				ignoreFileTypeSet.add(s.toLowerCase());
			}
		}
		watcher = FileSystems.getDefault().newWatchService();
		WatchKey key = homeDir.toPath().register(watcher, ENTRY_CREATE,
		        ENTRY_DELETE, ENTRY_MODIFY);
		registeredPaths.put(key, homeDir.toPath());
		logger.info("Watching QUESTION_TYPE_HOME: {}", homeDir);
		File[] childFolders = homeDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File childFolder : childFolders) {
			if (isQuestionTypeFolder(childFolder)) {
				registerAll(childFolder.toPath());
				QuestionTypeUtil.loadQuestionTypeConfig(childFolder);
			}
		}
		running = true;
		start();
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path pathToWatch) throws IOException {
		if (pathToWatch.getNameCount() == 2) {
			String name = pathToWatch.toFile().getName();
			questionTypeNames.add(name);
			logger.info("Registerd question type: {}", name);
		}
		WatchKey key = pathToWatch.register(watcher, ENTRY_CREATE,
		        ENTRY_DELETE, ENTRY_MODIFY);
		registeredPaths.put(key, pathToWatch);
		logger.info("Watching: {}", pathToWatch);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
			        BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public void add(QuestionTypeChangeListener listener) {
		listeners.add(listener);
		logger.info("Added listener: {}", listener.toString());
	}

	private boolean isQuestionTypeFolder(File folder) {
		File confFile = new File(folder, "config.xml");
		return confFile.exists();
	}

	@Override
	public void run() {
		logger.info("QuestionTypeChangeDetector started.");
		while (running) {
			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				break;
			}
			Path dir = registeredPaths.get(key);
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				// This key is registered only
				// for ENTRY_CREATE events,
				// but an OVERFLOW event can
				// occur regardless if events
				// are lost or discarded.
				if (kind == OVERFLOW) {
					continue;
				}

				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();
				Path path = dir.resolve(filename);

				if (kind == ENTRY_CREATE) {
					if (shouldIgnore(path))
						continue;
					String questionTypeName = getQuestionTypeName(path);
					logger.debug("{}: {} - {}", kind, questionTypeName, path);
					try {
						if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
							registerAll(path);
							for (QuestionTypeChangeListener listener : listeners) {
								listener.onCreateQuestionType(questionTypeName,
								        path.toFile());
							}
						}
					} catch (IOException x) {
						logger.error(x.getMessage());
					}
				} else if (kind == ENTRY_MODIFY) {
					String questionTypeName = getQuestionTypeName(path);
					logger.debug("{}: {} - {}", kind, questionTypeName,
					        path.toFile());
					if (shouldIgnore(path))
						continue;
					for (QuestionTypeChangeListener listener : listeners) {
						listener.onModifyQuestionType(questionTypeName,
						        path.toFile());
					}
				} else if (kind == ENTRY_DELETE) {
					logger.debug("Un-watch: {}", path);
					if (shouldIgnore(path))
						continue;
					String questionTypeName = getQuestionTypeName(path);
					for (QuestionTypeChangeListener listener : listeners) {
						listener.onDeleteQuestionType(questionTypeName,
						        path.toFile());
					}
				}
			}

			// Reset the key -- this step is critical if you want to
			// receive further watch events.  If the key is no longer valid,
			// the directory is inaccessible so exit the loop.
			boolean valid = key.reset();
			if (!valid) {
				registeredPaths.remove(key);
			}
		}
		logger.info("QuestionTypeChangeDetector stopped.");
	}

	private String getQuestionTypeName(Path path) {
		Path p = path.subpath(homeDir.toPath().getNameCount(),
		        path.getNameCount());
		return p.getName(0).toString();
	}

	private boolean shouldIgnore(Path path) {
		if (path.getNameCount() == 0)
			return false;
		String fname = path.getName(path.getNameCount() - 1).toString();
		int p = fname.lastIndexOf(".");
		if (p <= 0 || p == fname.length() - 1)
			return false;
		String ext = fname.substring(p + 1);
		return ignoreFileTypeSet.contains(ext.toLowerCase());
	}

	public static void main(String[] args) {
		try {
			QuestionTypeChangeDetector detector = new QuestionTypeChangeDetector();
			detector.init();
			Thread.sleep(60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
