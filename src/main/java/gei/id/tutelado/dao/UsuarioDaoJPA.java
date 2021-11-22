package gei.id.tutelado.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.model.Usuario;

public class UsuarioDaoJPA implements UsuarioDao {
	
	private EntityManagerFactory emf; 
	private EntityManager em;

	@Override
	public void setup (Configuracion config) {
		this.emf = (EntityManagerFactory) config.get("EMF");
	}
	
	@Override
	public Usuario almacena(Usuario usuario) {
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			em.persist(usuario);
			
			em.getTransaction().commit();
			em.close();
		} catch (Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return usuario;
	}
	
	@Override
	public Usuario modifica(Usuario usuario) {
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			em.merge(usuario);
			
			em.getTransaction().commit();
			em.close();
		} catch (Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (usuario);
	}
	
	@Override
	public void elimina(Usuario usuario) {
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			Usuario usuarioTmp = em.find(Usuario.class, usuario.getId());
			em.remove(usuarioTmp);

			em.getTransaction().commit();
			em.close();		
		} catch (Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
	}
	
	@Override
	public Usuario recuperaPorDni(String dni) {
		List<Usuario> usuarios = null;
		
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			usuarios = em.createNamedQuery("Usuario.findByDni", Usuario.class).setParameter("dni", dni).getResultList();
			
			em.getTransaction().commit();
			em.close();
		} catch (Exception ex) {
			if (em!=null && em.isOpen()) {
				if (em.getTransaction().isActive()) em.getTransaction().rollback();
				em.close();
				throw(ex);
			}
		}
		return (usuarios.size() != 0 ? usuarios.get(0) : null);
	}
	
}