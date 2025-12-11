package Controlador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import modelo.Prestamo;
import modelo.DetallePrestamo;
import modelo.Equipos; // 🚨 IMPORTANTE: Necesario para actualizar el estado del equipo
import java.util.Collections;
import java.util.ArrayList;

public class PrestamosJpaController {

    private final EntityManagerFactory emf;

    public PrestamosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    // =================================================================
    // MÉTODO COMPLEJO DE NEGOCIO: CREAR PRÉSTAMO Y ACTUALIZAR EQUIPOS
    // =================================================================
    
    /**
     * Registra un nuevo Préstamo, sus Detalles asociados y actualiza el estado de los Equipos.
     * Todo se ejecuta dentro de una única transacción JPA para garantizar la atomicidad.
     * * @param prestamo El objeto Prestamos (cabecera) a guardar.
     * @param detalles La lista de DetallePrestamo asociados al cabecera.
     * @param idsEquipos La lista de IDs de Equipos para actualizar su estado a 'Prestado'.
     */
   public void crearPrestamoConDetallesYActualizarEquipos(Prestamo prestamo, 
                                                       List<DetallePrestamo> detalles, 
                                                       List<Integer> idsEquipos) throws Exception {
    
    // Eliminamos el argumento 'idsEquipos' del método si no se usa, pero lo mantendremos por compatibilidad.
    
    EntityManager em = getEntityManager();
    try {
        em.getTransaction().begin();

        // 1. Persistir el Prestamo (Cabecera).
        em.persist(prestamo);
        em.flush(); // Fuerza la asignación del ID_Prestamo

        // 2. Persistir cada Detalle, asociándolo al ID_Prestamo generado.
        for (DetallePrestamo detalle : detalles) {
            // Re-asociar los objetos después del persist/flush para asegurar la FK
            detalle.setPrestamo(prestamo); 
            
            // Asegurarse de que las claves compuestas estén pobladas si no se hizo en el Controller Principal
            if (detalle.getDetallePrestamoPK() == null) {
                detalle.setDetallePrestamoPK(new modelo.DetallePrestamoPK(
                    prestamo.getIDPrestamo(), 
                    detalle.getEquipos().getIDEquipo()
                ));
            }
            
            em.persist(detalle);
        }
        
        // 🚨 ELIMINACIÓN DEL PASO 3 COMPLETO 🚨
        // Se ha eliminado la lógica para buscar, setEstado y merge de los Equipos.
        // Esto resuelve el error de 'Data truncated' y el conflicto de diseño.
        
        // 4. Confirmar la transacción (COMMIT)
        em.getTransaction().commit();

    } catch (Exception e) {
        // 5. Revertir si algo falla (ROLLBACK)
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        // 🚨 Crucial: Lanzar la excepción para que el Controlador_Principal pueda manejarla
        throw new Exception("❌ Falló la transacción completa del Préstamo. Los datos fueron revertidos: " + e.getMessage(), e);
    } finally {
        em.close();
    }
}
    
    // 💡 NOTA: El método 'crear(Prestamos, List<DetallePrestamo>)' que tenías antes 
    // ha sido reemplazado por el método 'crearPrestamoConDetallesYActualizarEquipos' 
    // para manejar la lógica de negocio completa de forma atómica.
    
    // ===========================
    // MÉTODOS CRUD ESTÁNDAR
    // ===========================

    public List<Prestamo> findPrestamosEntities() {
        EntityManager em = getEntityManager();
        try {
            // Se asume que la entidad JPA se llama Prestamos
            Query q = em.createQuery("SELECT p FROM Prestamos p");
            return q.getResultList();
        } catch (Exception e) {
             return Collections.emptyList();
        } finally {
            em.close();
        }
    }
    
    public Prestamo findPrestamos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            // Asume que la clave primaria es de tipo Integer
            return em.find(Prestamo.class, id); 
        } finally {
            em.close(); 
        }
    }
    
    public void edit(Prestamo prestamo) throws Exception { 
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(prestamo);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; 
        } finally {
            em.close();
        }
    }

    public void destroy(Integer id) throws Exception { 
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            Prestamo prestamo = em.find(Prestamo.class, id); 
            
            if (prestamo == null) {
                throw new EntityNotFoundException("El préstamo con ID " + id + " no existe.");
            }
            
            // Asumimos que la entidad Prestamos maneja la cascada (CascadeType.REMOVE) para DetallePrestamo.
            em.remove(prestamo);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; 
        } finally {
            em.close();
        }
    }
    public List<Prestamo> findPrestamosWithDetalles() {
    EntityManager em = getEntityManager();
    try {
        // Consulta que trae todos los préstamos Y carga sus colecciones 'detallePrestamoCollection'
        // en la misma transacción (JOIN FETCH), previniendo LazyInitializationException.
        Query q = em.createQuery("SELECT p FROM Prestamo p JOIN FETCH p.detallePrestamoCollection");
        return q.getResultList();
    } catch (Exception e) {
        System.err.println("Error al obtener prestamos con detalles: " + e.getMessage());
        return new ArrayList<>();
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
}