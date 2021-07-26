package br.com.caelum.carangobom.dashboard;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
public class DashboardControllerTest {

    @BeforeEach
    public void mockConfig() {
        openMocks(this);
    }

    @Test
    public void shouldConvertTheListReturnedList(){
        List<List<Object>> dashboardList =
                Arrays.asList(
                        Arrays.asList("Brand 1", new BigInteger(String.valueOf(2)), new BigDecimal(73020000)),
                        Arrays.asList("Brand 2", new BigInteger(String.valueOf(3)), new BigDecimal(54000000))
                );
    }
}
