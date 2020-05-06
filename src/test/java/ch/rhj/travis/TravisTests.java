package ch.rhj.travis;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Base64;
import java.util.Base64.Encoder;

import org.junit.jupiter.api.Test;

import ch.rhj.io.IO;
import ch.rhj.util.Cfg;
import ch.rhj.util.SysProps;

public class TravisTests {

	@Test
	public void testProjectSetup() {

		Cfg cfg = Cfg.system();

		String token = cfg.get("TRAVIS_TOKEN");
		String gpgPassphrase = cfg.get("GPG_PASSPHRASE");
		String sonatypeUsername = cfg.get("SONATYPE_USERNAME");
		String sonatypePassword = cfg.get("SONATYPE_PASSWORD");

		assertNotNull(token);
		assertNotNull(gpgPassphrase);
		assertNotNull(sonatypeUsername);
		assertNotNull(sonatypePassword);

		Encoder encoder = Base64.getEncoder();
		String gpgSecretKeys = encoder.encodeToString(IO.read(SysProps.userHome().resolve(".keys/rhjoerg.asc")));
		String gpgOwnerTrust = encoder.encodeToString(IO.read(SysProps.userHome().resolve(".keys/ownertrust.asc")));
		Travis travis = Travis.instance();

		assertEquals(token, travis.login(token));

		for (Repository repository : travis.repositories()) {

			if (!repository.active)
				continue;

			repository.env("SONATYPE_USERNAME", sonatypeUsername);
			repository.env("SONATYPE_PASSWORD", sonatypePassword);

			repository.env("GPG_SECRET_KEYS", gpgSecretKeys);
			repository.env("GPG_OWNERTRUST", gpgOwnerTrust);

			repository.env("GPG_EXECUTABLE", "gpg");
			repository.env("GPG_PASSPHRASE", gpgPassphrase);

			System.out.println(format("configured '%1$s'", repository.name));
		}
	}
}
