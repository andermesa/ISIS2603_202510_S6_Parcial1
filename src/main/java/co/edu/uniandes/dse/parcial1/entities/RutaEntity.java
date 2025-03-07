package co.edu.uniandes.dse.parcial1.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class RutaEntity extends BaseEntity {

    private String nombre;
    private String color;
    private String tipo;

    @ManyToMany
    @JoinTable(
        name = "ruta_estacion",
        joinColumns = @JoinColumn(name = "ruta_id"),
        inverseJoinColumns = @JoinColumn(name = "estacion_id")
    )
    private List<EstacionEntity> estaciones = new ArrayList<>();
}


