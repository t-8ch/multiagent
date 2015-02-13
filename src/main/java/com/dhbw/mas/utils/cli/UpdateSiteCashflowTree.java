package com.dhbw.mas.utils.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.dhbw.mas.cli.FileConverter;

public class UpdateSiteCashflowTree {

	@Parameter(names = { "--help", "-h" }, help = true)
	private boolean help;

	@Parameter(names = "--target", converter = FileConverter.class)
	private File target;

	@Parameter(names = "--source", converter = FileConverter.class)
	private File source;

	@Parameter(names = "--prefix")
	private String prefix = "";

	private void run() throws IOException {
		Path sourcepath = source.toPath();

		class TreeVisitor extends SimpleFileVisitor<Path> {
			StringBuilder sb;
			int indent;
			Path source;

			public TreeVisitor(Path source) {
				this.sb = new StringBuilder();
				this.indent = 0;
				this.source = source;
			}

			private void addNewline() {
				this.sb.append('\n');
			}

			private void addFile(String name, Path path) {
				this.sb.append(
						String.format(
								"* [`%s`](CashFlows/%s)", name, path.toString()));
				this.addNewline();
			}

			private void writeHeading(String text) {
				this.addNewline();

				for (int i = 0; i < this.indent; i++) {
					sb.append('#');
				}

				sb.append(' ');
				sb.append('`');
				this.sb.append(text);
				sb.append('`');
				this.addNewline();
				this.addNewline();
			}

			private void descendDirectory(String name) {
				this.writeHeading(name);
				this.indent++;
			}

			private void ascendDirectory() {
				this.indent--;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) {
				this.addFile(file.getFileName().toString(), source.relativize(file));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				this.ascendDirectory();
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				this.descendDirectory(dir.getFileName().toString());
				return FileVisitResult.CONTINUE;
			}

			public String getString() {
				return this.sb.toString();
			}
		}

		TreeVisitor visitor = new TreeVisitor(sourcepath);

		Files.walkFileTree(sourcepath, visitor);

		Path targetpath = target.toPath();

		target.mkdirs();
		Files.write(targetpath, visitor.getString().getBytes(),
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static void main(String[] args) throws Exception {
		UpdateSiteCashflowTree updater = new UpdateSiteCashflowTree();
		JCommander c = new JCommander(updater, args);
		c.setProgramName(System.getProperty("app.name",
				UpdateSiteCashflowTree.class.getCanonicalName()));

		if (updater.help) {
			c.usage();
		} else {
			updater.run();
		}
	}
}