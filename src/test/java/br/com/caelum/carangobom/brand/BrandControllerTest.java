package br.com.caelum.carangobom.brand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class BrandControllerTest {

    private BrandController brandController;
    private UriComponentsBuilder uriBuilder;

    @Mock
    private BrandRepository brandRepository;

    @BeforeEach
    public void mockConfig() {
        openMocks(this);

        brandController = new BrandController(brandRepository);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");
    }

    @Test
    void shouldReturnListWhenThereAreResults() {
        List<Brand> brands = Arrays.asList(
                new Brand(1L, "Audi"),
                new Brand(2L, "BMW"),
                new Brand(3L, "Fiat")
        );

        Page<Brand> pagedBrands = new PageImpl<Brand>(brands);

        PageRequest pageable = PageRequest.of(1, 10);

        when(brandRepository.findAll(pageable))
                .thenReturn(pagedBrands);

        Page<Brand> resultado = brandController.findAll(pageable);
        assertEquals(pagedBrands, resultado);
    }

    @Test
    void shouldReturnBrandById() {
        Brand audi = new Brand(1L, "Audi");

        when(brandRepository.findById(1L))
                .thenReturn(Optional.of(audi));

        ResponseEntity<Brand> resposta = brandController.findById(1L);
        assertEquals(audi, resposta.getBody());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenRetrieveNonexistentId() {
        when(brandRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Brand> resposta = brandController.findById(1L);
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void shouldReturnCreatedAndLocationWhenCreateBrand() {
        Brand nova = new Brand("Ferrari");

        when(brandRepository.save(nova))
                .then(invocation -> {
                    Brand marcaSalva = invocation.getArgument(0, Brand.class);
                    marcaSalva.setId(1L);

                    return marcaSalva;
                });

        ResponseEntity<Brand> resposta = brandController.save(nova, uriBuilder);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals("http://localhost:8080/brands/1", resposta.getHeaders().getLocation().toString());
    }

    @Test
    void shouldUpdateBrandWhenExists() {
        Brand audi = new Brand(1L, "Audi");

        when(brandRepository.findById(1L))
                .thenReturn(Optional.of(audi));

        ResponseEntity<Brand> resposta = brandController.update(1L, new Brand(1L, "NOVA Audi"));
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        Brand marcaAlterada = resposta.getBody();
        assertEquals("NOVA Audi", marcaAlterada.getName());
    }

    @Test
    void shouldNotUpdateBrandWhenDoesNotExist() {
        when(brandRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Brand> resposta = brandController.update(1L, new Brand(1L, "NOVA Audi"));
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void shouldDeleteBrandWithExists() {
        Brand audi = new Brand(1l, "Audi");

        when(brandRepository.findById(1L))
                .thenReturn(Optional.of(audi));

        ResponseEntity<Brand> resposta = brandController.delete(1L);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(brandRepository).delete(audi);
    }

    @Test
    void shouldNotDeleteBrandWhenDoesNotExist() {
        when(brandRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ResponseEntity<Brand> resposta = brandController.delete(1L);
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());

        verify(brandRepository, never()).delete(any());
    }

}
