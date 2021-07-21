package br.com.caelum.carangobom.vehicle;

import org.junit.jupiter.api.Test;
import br.com.caelum.carangobom.brand.Brand;
import org.junit.Assert;
import java.math.BigDecimal;
class VehicleFormTest {

    @Test
    void shouldConvertVehicleFormToVehicleIfBrandExist() throws Exception {
    	
    	Brand brand = new Brand(1L, "Audi");
    	
    	VehicleForm vehicleForm = new VehicleForm(brand.getId(), 2018, "TT", new BigDecimal(3500.95));
        Vehicle result = vehicleForm.toVehicle(brand);
        
        Vehicle expected = new Vehicle(brand, 2018, "TT", new BigDecimal(3500.95));

        Assert.assertEquals(expected.getModel(), result.getModel());
		Assert.assertEquals(expected.getYear(), result.getYear());
		Assert.assertEquals(expected.getId(), result.getId());
		Assert.assertEquals(expected.getBrand().getName(), result.getBrand().getName());
		Assert.assertEquals(expected.getBrand().getId(), result.getBrand().getId());
    }
}


