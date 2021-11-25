package gei.id.tutelado;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import gei.id.tutelado.model.Prueba;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test3_Consultas {
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
    public void t1_CRUD_RecuperarPruebasPorNss() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		productorDatos.creaSanitariosSueltos();
		productorDatos.creaPacienteConPruebasCompletas();
		productorDatos.registraUsuarios();
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Paciente.recuperarPruebasPacientePorNss\n");
    	
    	// Situación de partida
    	// p0, p1, s0, s1, pru0, pru1, pru2 desligados
    	
    	List<Prueba> pruebasp0 = new ArrayList<Prueba>();
    	List<Prueba> queryResult = new ArrayList<Prueba>();
    	pruebasp0.add(productorDatos.pru0);
    	pruebasp0.add(productorDatos.pru2);
    	
    	queryResult = usuDao.recuperaPruebasPacientePorNss(productorDatos.p0.getNss());
    	Assert.assertEquals(pruebasp0.size(), queryResult.size());
    	Assert.assertEquals(pruebasp0.contains(productorDatos.pru0), queryResult.contains(productorDatos.pru0));
    	Assert.assertEquals(pruebasp0.contains(productorDatos.pru2), queryResult.contains(productorDatos.pru2));
    }
    
    
    public void t2_CRUD_RecuperarPacienteSinPruebas() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Paciente.recuperarPacienteSinPruebas\n");
    	
    	
    }
    
    
    public void t3_CRUD_NumeroPositivosLocalidad() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Prueba.numeroPositivosLocalidad\n");
    	
    	
    }
    
    
    public void t4_CRUD_localidadMasTests() {
    	log.info("");	
		log.info("Configurando situación de partida do test -----------------------------------------------------------------------");
		
		
		log.info("");	
		log.info("Inicio do test --------------------------------------------------------------------------------------------------");
    	log.info("Obxectivo: Proba da consulta Prueba.localidadMasTests\n");
    	
    	
    }
}
