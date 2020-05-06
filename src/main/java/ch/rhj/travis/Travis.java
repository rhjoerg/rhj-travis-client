package ch.rhj.travis;

import static ch.rhj.jruby.RubyBuilder.rubyBuilder;
import static ch.rhj.util.Singleton.singleton;

import java.util.ArrayList;
import java.util.List;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

import ch.rhj.util.Singleton;

public class Travis {

	private static final Singleton<Travis> instance = singleton(Travis::new);

	private final Ruby ruby;

	private Travis() {

		ruby = rubyBuilder().require("travis", "1.9.1.travis.1208.9").build();
		ruby.evalScriptlet("require 'travis'");
	}

	public Ruby ruby() {

		return ruby;
	}

	public String login(String token) {

		String script = String.format("Travis.access_token = '%1$s'", token);

		return ruby.evalScriptlet(script).asJavaString();
	}

	public List<Repository> repositories() {

		List<Repository> result = new ArrayList<>();

		IRubyObject[] repositories = ruby.evalScriptlet("Travis::User.current.repositories.collect { |r| [r.name, r.owner_name, r.active] }") //
				.toJava(IRubyObject[].class);

		for (IRubyObject repository : repositories) {

			IRubyObject[] values = repository.toJava(IRubyObject[].class);
			String name = values[0].asJavaString();
			String owner = values[1].asJavaString();
			boolean active = values[2] != null && values[2].isTrue();

			result.add(new Repository(this, name, owner, active));
		}

		return result;
	}

	public String env(String repository, String key, String value) {

		String script = String.format("Travis::Repository.find('%1$s').env_vars['%2$s'] = '%3$s'", repository, key, value);

		return ruby.evalScriptlet(script).asJavaString();
	}

	public static Travis instance() {

		return instance.get();
	}
}
