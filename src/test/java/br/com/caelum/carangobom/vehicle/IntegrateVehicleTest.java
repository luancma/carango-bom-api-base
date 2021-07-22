package br.com.caelum.carangobom.vehicle;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.caelum.carangobom.brand.Brand;
import br.com.caelum.carangobom.brand.BrandRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrateVehicleTest {

	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturn404WhenTryUpdateVehicleWithNonexistentBrandId() throws Exception {
		Brand brand = brandRepository.save(new Brand("Audi"));
		Vehicle vehicle = vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));

		URI uri = new URI("/vehicles/" + vehicle.getId());

		JSONObject body = new JSONObject();
		body.put("model", "TT");
		body.put("brandId", 9999);
		body.put("year", 2012);
		body.put("price", 35000);

		mockMvc.perform(MockMvcRequestBuilders
				.put(uri)
				.content(body.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(response -> Assert.assertEquals("Brand not found", response.getResolvedException().getMessage()))
		;
	}

	@Test
	public void shouldUpdateVehicleWithCorrectParameters() throws Exception {
		Brand brand = brandRepository.save(new Brand("Audi"));
		Vehicle vehicle = vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));

		URI uri = new URI("/vehicles/" + vehicle.getId());

		JSONObject expectedBody = new JSONObject();
		expectedBody.put("brand", brand);
		expectedBody.put("model", "TT Updated");
		expectedBody.put("year", 2013);
		expectedBody.put("price", 40000);
		expectedBody.put("id", vehicle.getId());


		JSONObject body = new JSONObject();
		body.put("model", "TT Updated");
		body.put("brandId", brand.getId());
		body.put("year", 2013);
		body.put("price", 40000);


		mockMvc.perform(MockMvcRequestBuilders
				.put(uri)
				.content(body.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldCreateANewVehicle() throws Exception {

		Brand brand = brandRepository.save(new Brand("Audi"));

		URI uri = new URI("/vehicles");

		JSONObject body = new JSONObject();
		body.put("model", "TT");
		body.put("brandId", brand.getId());
		body.put("year", 2010);
		body.put("price", 35000.50);

		mockMvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(body.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void shouldReturn404WhenTryCreateVehicleWithNonexistentBrandId() throws Exception {

		URI uri = new URI("/vehicles");

		JSONObject body = new JSONObject();
		body.put("model", "TT");
		body.put("brandId", 999L);
		body.put("year", 2012);
		body.put("price", 35000);

		mockMvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(body.toString())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void shouldDeleteVehicleWithCorrectVehicleId() throws Exception {

		Brand brand = brandRepository.save(new Brand("Audi"));
		Vehicle vehicle = vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));

		URI uri = new URI("/vehicles/" + vehicle.getId());

		mockMvc.perform(MockMvcRequestBuilders
				.delete(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturn404WhenTryDeleteVehicleWithNonexistentVehicleId() throws Exception {

		Brand brand = brandRepository.save(new Brand("Audi"));
		vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));

		URI uri = new URI("/vehicles/" + 999);

		mockMvc.perform(MockMvcRequestBuilders
				.delete(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void shouldReturn200WhenFindAllVehicles() throws Exception {
		URI uri = new URI("/vehicles");

		mockMvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturn200WhenFindOneVehicle() throws Exception {
		Brand brand = brandRepository.save(new Brand("Audi"));
		Vehicle vehicle = vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));
		URI uri = new URI("/vehicles/" + vehicle.getId());


		mockMvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void shouldReturn404WhenTryFindOneWithNonexistentVehicleId() throws Exception {
		Brand brand = brandRepository.save(new Brand("Audi"));
		Vehicle vehicle = vehicleRepository.save(new Vehicle(brand, 2012, "TT", new BigDecimal(3500.95)));
		URI uri = new URI("/vehicles/" + 999);

		mockMvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	    
}
