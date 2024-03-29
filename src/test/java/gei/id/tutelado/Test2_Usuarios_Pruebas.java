package gei.id.tutelado;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

import gei.id.tutelado.configuracion.Configuracion;
import gei.id.tutelado.configuracion.ConfiguracionJPA;
import gei.id.tutelado.dao.PruebaDao;
import gei.id.tutelado.dao.PruebaDaoJPA;
import gei.id.tutelado.dao.UsuarioDao;
import gei.id.tutelado.dao.UsuarioDaoJPA;
import gei.id.tutelado.model.Paciente;
import gei.id.tutelado.model.Prueba;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test2_Usuarios_Pruebas {
	private Logger log = LogManager.getLogger("gei.id.tutelado");

    private static ProductorDatosPrueba productorDatos = new ProductorDatosPrueba();

    private static Configuracion cfg;
    private static UsuarioDao usuDao;
    private static PruebaDao pruebaDao;

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            log.info("");
            log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            log.info("Iniciando test: " + description.getMethodName());
            log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        }
        protected void finished(Description description) {
            log.info("");
            log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
            log.info("Finalizado test: " + description.getMethodName());
            log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
        }
    };

    @BeforeClass
    public static void init() throws Exception {
        cfg = new ConfiguracionJPA();
        cfg.start();

        usuDao = new UsuarioDaoJPA();
        usuDao.setup(cfg);
        
        pruebaDao = new PruebaDaoJPA();
        pruebaDao.setup(cfg);

        productorDatos = new ProductorDatosPrueba();
        productorDatos.Setup(cfg);
    }

    @AfterClass
    public static void endclose() throws Exception {
        cfg.endUp();
    }

    @Before
    public void setUp() throws Exception {
        log.info("");
        log.info("Limpando a BD ----------------------------------------------------------------------------------------");
        productorDatos.limpiaBD();
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void t1_CRUD_TestAlmacena() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea p0, p1, pSinPruebas
		productorDatos.creaPacientesSueltos();
		// Rexistro creacións na bd
		productorDatos.registraUsuarios();
		// Crea pru0, pru1, pru2
		productorDatos.creaPruebasSueltas();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da gravación de probas\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Primeira proba vinculada a un paciente\n"
    			+ "\t\t\t\t b) Nova proba para un paciente con probas previas\n"); 
    	
    	// Situación de partida
    	// p0 desligado, pru0 e pru1 transitorios
    	productorDatos.p0.addPrueba(productorDatos.pru0);
    	log.info("");	
		log.info("Gravando primeira proba dun paciente\n");
		
		Assert.assertNull(productorDatos.pru0.getId());
		pruebaDao.almacena(productorDatos.pru0);
		Assert.assertNotNull(productorDatos.pru0.getId());
		
		productorDatos.p0.addPrueba(productorDatos.pru1);
		log.info("");	
		log.info("Gravando nova proba para paciente con probas previas\n");
		
		Assert.assertNull(productorDatos.pru1.getId());
		pruebaDao.almacena(productorDatos.pru1);
		Assert.assertNotNull(productorDatos.pru1.getId());
    }
    
    @Test
    public void t2a_CRUD_TestRecupera() {
    	Prueba p;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea p0, p1, pSinPruebas, pru0, pru1, pru2
		productorDatos.creaPacienteConPruebas();
		// Rexistra creacións na bd
		productorDatos.registraUsuarios();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de recuperación (por código) de probas\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Recuperación por código existente\n"
    			+ "\t\t\t\t b) Recuperación por código inexistente\n"); 
    	
    	// Situación de partida
    	// pru0 desligado
    	
    	log.info("Recuperando proba con código existente\n");
    	
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	Assert.assertEquals(productorDatos.pru0.getCodPrueba(), p.getCodPrueba());
    	Assert.assertEquals(productorDatos.pru0.getTipo(), p.getTipo());
    	Assert.assertEquals(productorDatos.pru0.getFecha(), p.getFecha());
    	
    	log.info("Recuperando proba con código inexistente\n");
    	p = pruebaDao.recuperaPorCod("inexistente");
    	Assert.assertNull(p);
    }
    
    @Test
    public void t2b_CRUD_TestRecupera() {
    	Prueba p;
    	boolean excepcion;
    	Paciente pac;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea s0, s1
		productorDatos.creaSanitariosSueltos();
		// Crea p0, p1, pSinpruebas, pru0, pru1, pru2
		productorDatos.creaPacienteConPruebasCompletas();
		// Rexistra creacións na bd
		productorDatos.registraUsuarios();
		
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da recuperación de propiedades LAZY\n"   
		+ "\t\t\t\t Casos contemplados:\n"
		+ "\t\t\t\t a) Recuperación de proba con colección (LAZY) de String \n"
		+ "\t\t\t\t b) Carga forzada de colección LAZY da dita coleccion\n"   
		+ "\t\t\t\t c) Recuperación de proba con carga LAZY de paciente\n"
		+ "\t\t\t\t d) Carga forzada de paciente LAZY da dita proba\n"
		+ "\t\t\t\t e) Recuperacion de proba con referencia EAGER a sanitario\n"
		+ "\t\t\t\t f) Recuperacion de paciente con referencia EAGER á colección de probas\n"
		+ "\t\t\t\t g) Encadeamento EAGER\n");
    	
    	// Situación de partida
    	// pr0 desligado
    	
    	log.info("Probando excepción tras recuperación LAZY. Caso a)\n");
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	log.info("Accedendo á colección LAZY de síntomas\n");
    	try {
    		Assert.assertEquals(productorDatos.sintomas.size(), p.getSintomas().size());
    		excepcion = false;
    	} catch(LazyInitializationException ex) {
    		excepcion = true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
    	log.info("Probando carga forzada de colección LAZY\n");
    	
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	p = pruebaDao.restauraSintomas(p);
    	Assert.assertEquals(productorDatos.sintomas.size(), p.getSintomas().size());
    	
    	log.info("");
    	log.info("Probando excepción tras recuperación LAZY. Caso c)\n");
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	log.info("Accedendo a paciente LAZY\n");
    	try {
    		Assert.assertEquals(productorDatos.pru0.getPaciente().getDni(), p.getPaciente().getDni());
    		excepcion = false;
    	} catch (LazyInitializationException ex) {
    		excepcion = true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
    	log.info("Probando carga forzada de paciente LAZY\n");
    	
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	p = pruebaDao.restauraPaciente(p);
    	Assert.assertEquals(productorDatos.pru0.getPaciente().getDni(), p.getPaciente().getDni());
    	
    	log.info("");
    	log.info("Recuperacion de proba con referencia EAGER a sanitario\n");
    	
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	Assert.assertEquals(productorDatos.pru0.getSanitario().getDni(), p.getSanitario().getDni());
    	
    	log.info("");
    	log.info("Recuperacion de paciente con referencia EAGER á colección de probas\n");
    	
    	// Situación de partida
    	// p0 desligado
    	
    	pac = (Paciente) usuDao.recuperaPorDni(productorDatos.p0.getDni());
    	Assert.assertEquals(productorDatos.p0.getPruebas().first(), pac.getPruebas().first());
    	
    	log.info("");
    	log.info("Probando encadeamento EAGER dende paciente ao sanitario da proba dun paciente\n");
    	Assert.assertEquals(productorDatos.p0.getPruebas().first().getSanitario().getDni(), pac.getPruebas().first().getSanitario().getDni());
    	
    }
    
    @Test
    public void t3_CRUD_TestModifica() {
    	Set<String> sintomas = new HashSet<String>();
    	sintomas.add("Tos");
    	sintomas.add("Cansancio");
    	
    	Prueba p, pModif;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		// Crea p0, p1, pSinpruebas, pru0, pru1, pru2
		productorDatos.creaPacienteConPruebas();
		// Rexistra creacións na bd
		productorDatos.registraUsuarios();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de modificación da información dunha proba \n");

    	// Situación de partida
    	// pru0 desligado
    	p = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	p = pruebaDao.restauraSintomas(p);
    	Assert.assertNotEquals(sintomas, p.getSintomas());
    	p.setSintomas(sintomas);
    	pruebaDao.modifica(p);
    	pModif = pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba());
    	pModif = pruebaDao.restauraSintomas(pModif);
    	Assert.assertEquals(sintomas, pModif.getSintomas());
    }
    
    @Test
    public void t4a_CRUD_TestElimina() {

    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea p0, p1, pSinpruebas, pru0, pru1, pru2
		productorDatos.creaPacienteConPruebas();
		// Rexistra creacións na bd
		productorDatos.registraUsuarios();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de proba (asignada a paciente)\n");
    	
    	// Situación de partida
    	// pru0 desligado
    	
    	Assert.assertNotNull(pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba()));
    	pruebaDao.elimina(productorDatos.pru0);
    	Assert.assertNull(pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba()));
    }
    
    @Test
    public void t4b_CRUD_TestElimina() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		// Crea p0, p1, pSinpruebas, pru0, pru1, pru2
		productorDatos.creaPacienteConPruebas();
		// Rexistra creacións na bd
		productorDatos.registraUsuarios();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de eliminación de paciente con probas asignadas\n");
    	
    	// Situación de partida
    	// p0, pru0, pru2 desligado
    	
    	Assert.assertNotNull(usuDao.recuperaPorDni(productorDatos.p0.getDni()));
    	Assert.assertNotNull(pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba()));
    	Assert.assertNotNull(pruebaDao.recuperaPorCod(productorDatos.pru2.getCodPrueba()));
    	usuDao.elimina(productorDatos.p0);
    	Assert.assertNull(usuDao.recuperaPorDni(productorDatos.p0.getDni()));
    	Assert.assertNull(pruebaDao.recuperaPorCod(productorDatos.pru0.getCodPrueba()));
    	Assert.assertNull(pruebaDao.recuperaPorCod(productorDatos.pru2.getCodPrueba()));
    }
    
    @Test
    public void t5_CRUD_TestExcepciones() {
    	boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violacion de restricions not null e unique\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Gravación de proba con código nulo\n"
    			+ "\t\t\t\t b) Gravación de proba con código duplicado\n");

    	// Crea p0, p1, pSinPruebas
    	productorDatos.creaPacientesSueltos();
    	// Rexistra creacións na bd
    	productorDatos.registraUsuarios();
    	// Crea pru0, pru1, pru2
    	productorDatos.creaPruebasSueltas();
    	
    	// Situación de partida
    	// p0 desligado, pru0 transitorio
    	
    	productorDatos.pru0.setCodPrueba(null);
    	productorDatos.p0.addPrueba(productorDatos.pru0);
    	
    	log.info("");
    	log.info("Probando gravación con código nulo\n");
    	
    	try {
    		pruebaDao.almacena(productorDatos.pru0);
    		excepcion = false;
    	} catch(Exception ex) {
    		excepcion = true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
    	log.info("Probando gravación con código duplicado\n");
    	
    	productorDatos.p1.addPrueba(productorDatos.pru2);
    	pruebaDao.almacena(productorDatos.pru2);
    	
    	productorDatos.pru1.setCodPrueba(productorDatos.pru2.getCodPrueba());
    	productorDatos.p1.addPrueba(productorDatos.pru1);
    	
    	try {
    		pruebaDao.almacena(productorDatos.pru1);
    		excepcion = false;
    	} catch(Exception ex) {
    		excepcion = true;
    		log.info(ex.getClass().getName());
    	}
    	Assert.assertTrue(excepcion);
    }
    
    @Test
    public void t5b_CRUD_TestExcepciones() {
    	boolean excepcion;
    	
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");

		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba de violacion de restricions de borrado\n"   
    			+ "\t\t\t\t Casos contemplados:\n"
    			+ "\t\t\t\t a) Borrado de probas que xa tiveron lugar\n"
    			+ "\t\t\t\t b) Borrado de sanitario que fixo probas\n");
    	
    	// Crea s0, s1
    	productorDatos.creaSanitariosSueltos();
    	// Crea p0, p1, pSinPruebas, pru0, pru1, pru2
    	productorDatos.creaPacienteConPruebasCompletas();
    	productorDatos.pru0.setFecha(LocalDateTime.of(2021, 11, 20, 13, 45));
    	// Rexistra creacións na bd
    	productorDatos.registraUsuarios();
    	
    	// Situación de partida
    	// pru0, s0 desligados
    	
    	log.info("");
    	log.info("Probando borrado de probas que xa tiveron lugar");
    	
    	try {
    		pruebaDao.elimina(productorDatos.pru0);
    		excepcion = false;
    	} catch (Exception ex) {
    		excepcion = true;
    		log.info(ex.getMessage());
    	}
    	Assert.assertTrue(excepcion);
    	
    	log.info("");
    	log.info("Probando borrado de sanitario que fixo probas");
    	
    	try {
    		usuDao.elimina(productorDatos.s0);
    		excepcion = false;
    	} catch (Exception ex) {
    		excepcion = true;
    		log.info(ex.getMessage());
    	}
    	Assert.assertTrue(excepcion);
    }
    
}
