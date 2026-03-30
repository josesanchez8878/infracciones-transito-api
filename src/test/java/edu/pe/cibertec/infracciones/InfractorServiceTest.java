package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.exception.InfractorNotFoundException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void verificarBloqueo_conDosVencidas_noBloquea() {
        Infractor infractor = new Infractor();
        infractor.setId(1L);
        infractor.setBloqueado(false);

        Multa v1 = new Multa(); v1.setEstado(EstadoMulta.VENCIDA);
        Multa v2 = new Multa(); v2.setEstado(EstadoMulta.VENCIDA);

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(multaRepository.findByInfractor_IdAndEstado(1L, EstadoMulta.VENCIDA))
                .thenReturn(List.of(v1, v2));

        assertDoesNotThrow(() -> infractorService.verificarBloqueo(1L));
        verify(infractorRepository, never()).save(any());
    }
}