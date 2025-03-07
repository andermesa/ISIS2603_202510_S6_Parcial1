package co.edu.uniandes.dse.parcial1.services;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.BusinessLogicException;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EstacionRutaServiceTest { 

    @Autowired
    private EstacionRutaService estacionRutaService; 

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    private EstacionEntity estacion;
    private RutaEntity rutaDiurna;
    private RutaEntity rutaNocturna;

    @BeforeEach
    void setUp() {
        estacion = new EstacionEntity();
        estacion.setNombre("Estación Central");
        estacion.setCapacidad(50);
        estacion = estacionRepository.save(estacion);

        rutaDiurna = new RutaEntity();
        rutaDiurna.setNombre("Ruta 1");
        rutaDiurna.setTipo("diurna");
        rutaDiurna = rutaRepository.save(rutaDiurna);

        rutaNocturna = new RutaEntity();
        rutaNocturna.setNombre("Ruta 2");
        rutaNocturna.setTipo("nocturna");
        rutaNocturna = rutaRepository.save(rutaNocturna);

        estacion.getRutas().add(rutaNocturna);
        estacionRepository.save(estacion);
    }

    

    @Test
    void testRemoveEstacionRuta_Exitoso() throws EntityNotFoundException, BusinessLogicException {
        estacionRutaService.addEstacionRuta(estacion.getId(), rutaDiurna.getId());

        estacionRutaService.removeEstacionRuta(estacion.getId(), rutaDiurna.getId());

        EstacionEntity estacionActualizada = estacionRepository.findById(estacion.getId()).orElseThrow();
        assertFalse(estacionActualizada.getRutas().contains(rutaDiurna));
    }

    @Test
    void testRemoveEstacionRuta_EstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> estacionRutaService.removeEstacionRuta(999L, rutaDiurna.getId()));
    }

    @Test
    void testRemoveEstacionRuta_RutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> estacionRutaService.removeEstacionRuta(estacion.getId(), 999L));
    }

    @Test
    void testRemoveEstacionRuta_NoSePuedeEliminarUltimaNocturna() {
        assertThrows(BusinessLogicException.class, () -> estacionRutaService.removeEstacionRuta(estacion.getId(), rutaNocturna.getId()));
    }

    

    @Test
    void testAddEstacionRuta_Exitoso() throws EntityNotFoundException, BusinessLogicException {
        estacionRutaService.addEstacionRuta(estacion.getId(), rutaDiurna.getId());

        EstacionEntity estacionActualizada = estacionRepository.findById(estacion.getId()).orElseThrow();
        assertTrue(estacionActualizada.getRutas().contains(rutaDiurna));
    }

    @Test
    void testAddEstacionRuta_EstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> estacionRutaService.addEstacionRuta(999L, rutaDiurna.getId()));
    }

    @Test
    void testAddEstacionRuta_RutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> estacionRutaService.addEstacionRuta(estacion.getId(), 999L));
    }

    @Test
    void testAddEstacionRuta_LimiteRutasCirculares() {
        RutaEntity rutaCircular1 = new RutaEntity();
        rutaCircular1.setNombre("Ruta Circular 1");
        rutaCircular1.setTipo("circular");
        rutaCircular1 = rutaRepository.save(rutaCircular1);

        RutaEntity rutaCircular2 = new RutaEntity();
        rutaCircular2.setNombre("Ruta Circular 2");
        rutaCircular2.setTipo("circular");
        rutaCircular2 = rutaRepository.save(rutaCircular2);

        estacion.getRutas().addAll(List.of(rutaCircular1, rutaCircular2));
        estacionRepository.save(estacion);

        RutaEntity nuevaRutaCircular = new RutaEntity();
        nuevaRutaCircular.setNombre("Ruta Circular 3");
        nuevaRutaCircular.setTipo("circular");
        nuevaRutaCircular = rutaRepository.save(nuevaRutaCircular);

        assertThrows(BusinessLogicException.class, () -> estacionRutaService.addEstacionRuta(estacion.getId(), nuevaRutaCircular.getId()));
    }
}
