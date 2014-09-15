package jenkins.plugins.elanceodesk.workplace.notifier.model;

import hudson.scm.ChangeLogSet.AffectedFile;

import java.util.Collection;

public class Changeset {

	String author;
	
	Collection<? extends AffectedFile> affectedFiles;

	public Changeset(String author, Collection<? extends AffectedFile> affectedFiles) {
		this.author = author;
		this.affectedFiles = affectedFiles;
	}
	
	public String getAuthor() {
		return author;
	}

	public Collection<? extends AffectedFile> getAffectedFiles() {
		return affectedFiles;
	}

}
