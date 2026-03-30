package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    // Pregunta 3
    @Test
    void procesarPago_mismodiaDEmision_aplicaDescuento20() {
        Multa multa = new Multa();
        multa.setId(1L);
        multa.setMonto(500.0);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(30));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var resultado = pagoService.procesarPago(1L);

        assertEquals(400.0, resultado.getMontoPagado());
        assertEquals(EstadoMulta.PAGADA, multa.getEstado());
    }

    // Pregunta 4
    @Test
    void procesarPago_multaVencida_aplicaRecargo15() {
        Multa multa = new Multa();
        multa.setId(1L);
        multa.setMonto(500.0);
        multa.setFechaEmision(LocalDate.now().minusDays(12));
        multa.setFechaVencimiento(LocalDate.now().minusDays(2));
        multa.setEstado(EstadoMulta.VENCIDA);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(pagoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        pagoService.procesarPago(1L);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository, times(1)).save(captor.capture());

        Pago pagado = captor.getValue();
        assertEquals(75.0, pagado.getRecargo());
        assertEquals(0.0, pagado.getDescuentoAplicado());
        assertEquals(575.0, pagado.getMontoPagado());
    }
}