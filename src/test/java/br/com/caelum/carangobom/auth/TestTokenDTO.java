package br.com.caelum.carangobom.auth;

import br.com.caelum.carangobom.auth.token.TokenDTO;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class TestTokenDTO {

    @Test
    void testAddingValues(){
        String tokenValue = "TOKEN";
        String typeValue = "type";

        TokenDTO tokenDTO = new TokenDTO(tokenValue, typeValue);

        String token = tokenDTO.getToken();
        String type = tokenDTO.getType();


        Assert.assertEquals(token, tokenValue);
        Assert.assertEquals(type, typeValue);
    }

}
