package co.edu.uniandes.dse.parcial1.services;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.BusinessLogicException;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EstacionRutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EstacionRepository estacionRepository;

    @Transactional
    public void removeEstacionRuta(Long rutaId, Long estacionId) throws EntityNotFoundException, BusinessLogicException {
        log.info("eliminar una ruta de una estación");

        
        Optional<EstacionEntity> estacionOpt = estacionRepository.findById(estacionId);
        if (estacionOpt.isEmpty()) {
            throw new EntityNotFoundException("La estación no existe");
        }
        EstacionEntity estacion = estacionOpt.get();

        
        Optional<RutaEntity> rutaOpt = rutaRepository.findById(rutaId);
        if (rutaOpt.isEmpty()) {
            throw new EntityNotFoundException("La ruta no existe");
        }
        RutaEntity ruta = rutaOpt.get();

        
        if (ruta.getTipo().equalsIgnoreCase("nocturna")) {
            long rutasNocturnas = estacion.getRutas().stream()
                    .filter(r -> r.getTipo().equalsIgnoreCase("nocturna"))
                    .count();
            if (rutasNocturnas <= 1) {
                throw new BusinessLogicException("No se puede eliminar la única ruta nocturna de una estación.");
            }
        }

        
        estacion.getRutas().remove(ruta);
        ruta.getEstaciones().remove(estacion);

        
        estacionRepository.save(estacion);
        rutaRepository.save(ruta);

        log.info("Finalizado eliminar una ruta de una estación");
    }

    
    @Transactional
    public void addEstacionRuta(Long rutaId, Long estacionId) throws EntityNotFoundException, BusinessLogicException {
        log.info("agregar una ruta a una estación");

        
        Optional<EstacionEntity> estacionOpt = estacionRepository.findById(estacionId);
        if (estacionOpt.isEmpty()) {
            throw new EntityNotFoundException("La estación no existe");
        }
        EstacionEntity estacion = estacionOpt.get();

        
        Optional<RutaEntity> rutaOpt = rutaRepository.findById(rutaId);
        if (rutaOpt.isEmpty()) {
            throw new EntityNotFoundException("La ruta no existe");
        }
        RutaEntity ruta = rutaOpt.get();

        
        if (ruta.getTipo().equalsIgnoreCase("circular") && estacion.getCapacidad() < 100) {
            long rutasCirculares = estacion.getRutas().stream()
                    .filter(r -> r.getTipo().equalsIgnoreCase("circular"))
                    .count();
            if (rutasCirculares >= 2) {
                throw new BusinessLogicException("Una estación con capacidad menor a 100 pasajeros no puede tener más de 2 rutas circulares.");
            }
        }

        
        estacion.getRutas().add(ruta);
        ruta.getEstaciones().add(estacion);

        
        estacionRepository.save(estacion);
        rutaRepository.save(ruta);

        log.info("Finalizado agregar una ruta a una estación");
    }
}
