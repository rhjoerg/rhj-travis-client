package ch.rhj.travis;

public class Repository {

	private final Travis travis;

	public final String name;
	public final String owner;
	public final boolean active;

	public final String repository;

	public Repository(Travis travis, String name, String owner, boolean active) {

		this.travis = travis;

		this.name = name;
		this.owner = owner;
		this.active = active;

		this.repository = owner + "/" + name;
	}

	public String env(String key, String value) {

		return travis.env(repository, key, value);
	}
}
