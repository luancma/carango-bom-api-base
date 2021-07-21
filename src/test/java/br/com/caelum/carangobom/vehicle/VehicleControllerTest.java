package br.com.caelum.carangobom.vehicle;

import br.com.caelum.carangobom.brand.Brand;
import br.com.caelum.carangobom.brand.BrandService;
import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import ch.qos.logback.core.net.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
class VehicleControllerTest {

    private VehicleController vehicleController;
    private UriComponentsBuilder uriBuilder;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private BrandService brandService;

    private VehicleForm vehicleForm;
    private Vehicle vehicle;
    private Brand brand;

    @BeforeEach
    public void mockConfig() {
        openMocks(this);
        this.vehicleController = new VehicleController(this.vehicleRepository, this.vehicleService);
        this.uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");
        this.vehicleForm = new VehicleForm(1L, 2012, "model", new BigDecimal(20000));
        this.brand = new Brand(1L, "Audi");
        this.vehicle = this.vehicleForm.toVehicle(this.brand);
    }

    @Test
    void shouldCreateVehicle() throws Exception {
        when(this.vehicleService.create(vehicleForm)).thenReturn(this.vehicle);

        ResponseEntity<Vehicle> result = vehicleController.create(vehicleForm, uriBuilder);
        assertEquals(result.getStatusCodeValue(), 201);
        Mockito.verify(this.vehicleService).create(vehicleForm);
    }

}
