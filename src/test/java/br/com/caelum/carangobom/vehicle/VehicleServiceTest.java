package br.com.caelum.carangobom.vehicle;

import br.com.caelum.carangobom.brand.Brand;
import br.com.caelum.carangobom.brand.BrandRepository;
import br.com.caelum.carangobom.brand.BrandService;
import br.com.caelum.carangobom.exception.BadRequestException;
import br.com.caelum.carangobom.exception.NotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class VehicleServiceTest {

    @Mock
    public VehicleRepository vehicleRepository;

    @Mock
    public BrandService brandService;

    public VehicleService vehicleService;

    public Brand brand;
    public VehicleForm vehicleForm;
    public Vehicle vehicle;

    @BeforeEach
    void mockConfig() {
        openMocks(this);

        brand = new Brand(1l, "Audi");
        vehicleForm = new VehicleForm(this.brand.getId(), 2012, "TT", new BigDecimal(35000.50));
        vehicle = new Vehicle(brand, 2012, "TT", new BigDecimal(35000.50));

        this.vehicleService = new VehicleService(this.vehicleRepository, this.brandService);
    }

    @Test
    void shouldCreateVehicle() throws Exception {

        when(this.brandService.findById(anyLong())).thenReturn(brand);

        when(this.vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = this.vehicleService.create(this.vehicleForm);

        Assert.assertEquals(vehicle.getPrice(), result.getPrice());
        Assert.assertEquals(vehicle.getModel(), result.getModel());
        Assert.assertEquals(vehicle.getYear(), result.getYear());
        Assert.assertEquals(vehicle.getBrand().getId(), result.getBrand().getId());
        Assert.assertEquals(vehicle.getBrand().getName(), result.getBrand().getName());
    }

    @Test
    void shouldReturn404WhenTryCreateVehicleWithNonexistentBrandId() throws Exception {
        when(this.brandService.findById(anyLong())).thenThrow(NotFoundException.class);

        Assert.assertThrows(NotFoundException.class, () -> {
            vehicleService.create(vehicleForm);
        });
    }

    @Test
    void shouldReturn404WhenTryUpdateVehicleWithNonexistentBrandId() throws Exception {
        when(this.brandService.findById(anyLong())).thenReturn(brand);
        when(this.vehicleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(vehicle));

        Assert.assertThrows(NotFoundException.class, () -> {
            vehicleService.update(vehicle.getId(), vehicleForm);
        });
    }

    @Test
    void shouldReturn404WhenTryUpdateVehicleWithNonexistentVehicleId() throws Exception {
        when(this.vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(this.brandService.findById(anyLong())).thenReturn(brand);

        Assert.assertThrows(NotFoundException.class, () -> {
            vehicleService.update(vehicle.getId(), vehicleForm);
        });
    }

    @Test
    void shouldReturnUpdatedVehicle() throws Exception {
        VehicleForm updatedVehicleForm = new VehicleForm(brand.getId(), 2000, "TT updated", new BigDecimal(50000));

        when(this.brandService.findById(anyLong())).thenReturn(brand);
        when(this.vehicleRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(vehicle));

        Vehicle result = this.vehicleService.update(1L, updatedVehicleForm);

        Assert.assertEquals(vehicle.getPrice(), result.getPrice());
        Assert.assertEquals(vehicle.getModel(), result.getModel());
        Assert.assertEquals(vehicle.getYear(), result.getYear());
        Assert.assertEquals(vehicle.getBrand().getId(), result.getBrand().getId());
        Assert.assertEquals(vehicle.getBrand().getName(), result.getBrand().getName());

    }

    @Test
    void shouldReturn404WhenTryFindByIdVehicleWithNonexistentVehicleId() throws Exception {
        when(this.vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assert.assertThrows(NotFoundException.class, () -> {
            vehicleService.findById(1L);
        });
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        when(this.vehicleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(vehicle));

        Vehicle result = this.vehicleService.delete(1L);

        Assert.assertEquals(vehicle.getPrice(), result.getPrice());
        Assert.assertEquals(vehicle.getModel(), result.getModel());
        Assert.assertEquals(vehicle.getYear(), result.getYear());
        Assert.assertEquals(vehicle.getBrand().getId(), result.getBrand().getId());
        Assert.assertEquals(vehicle.getBrand().getName(), result.getBrand().getName());
        Mockito.verify(this.vehicleRepository).delete(vehicle);

    }

    @Test
    void shouldReturn404WhenTryDeleteWhitNonexistentVehicleId() throws Exception {
        when(this.vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assert.assertThrows(NotFoundException.class, () -> vehicleService.delete(1L));
    }


    @Test
    void shouldReturnVehicleWhenTryFindById() throws Exception {
        when(this.vehicleRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(vehicle));

        Vehicle result = this.vehicleService.findById(1L);

        Assert.assertEquals(vehicle.getPrice(), result.getPrice());
        Assert.assertEquals(vehicle.getModel(), result.getModel());
        Assert.assertEquals(vehicle.getYear(), result.getYear());
        Assert.assertEquals(vehicle.getBrand().getId(), result.getBrand().getId());
        Assert.assertEquals(vehicle.getBrand().getName(), result.getBrand().getName());
    }

}