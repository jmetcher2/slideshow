package au.id.lagod.slideshow;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ImageFileLister extends SimpleFileVisitor<Path> {
	
	private List<Path> files;
	private PathMatcher matcher;

	public ImageFileLister(List<Path> files, String pattern) {
		this.files = files;
		matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            files.add(file);
        }
		return FileVisitResult.CONTINUE;
	}

	public List<Path> getFiles() {
		return files;
	}
	
	

}
