package br.com.caelum.carangobom.user;

import br.com.caelum.carangobom.auth.Profile;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
class ProfileTest {

    @Test
    void shouldTestProfileConstructor(){
        Optional<Profile> profile = Optional.of(new Profile());
        profile.get().setId(1L);
        profile.get().setName("Test name");

        Long profileId = profile.get().getId();
        String profileName = profile.get().getName();
        String profileAuth = profile.get().getAuthority();

        Assertions.assertEquals(1L, profileId.intValue());
        Assertions.assertEquals("Test name", profileName.toString());
        Assertions.assertEquals("Test name", profileAuth.toString());

    }
}
