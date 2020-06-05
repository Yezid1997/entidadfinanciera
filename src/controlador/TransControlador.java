package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.*;

public class TransControlador implements Initializable {
	
	ControladorGeneral controlGeneral = new ControladorGeneral();
		
	//Objetos para manejar la base de datos.
	Cliente objCliente = new Cliente(null,null,null,null,null);
	CuentaBancaria objCuentaBancaria = new CuentaBancaria();
	Garantia objGarantia = new Garantia();
	Garantias_Prestamo objGarantias_Prestamo = new Garantias_Prestamo();
	Inversion objInversion = new Inversion();
	Prestamo objPrestamo = new Prestamo();
	Transaccion objTransaccion = new Transaccion();
	
	//Generales	
	String cedulaCliente,tipoTrans;
	double monto,tasaEA,mensualidad;
	int numCuotas;
	LocalDate fechaIniciacion,fechaTermino,fechaSolicitud;
	
	ResultSet clienteRegistro;
	
	//Banderas
    private boolean cedulaOK = false;
    
	//Main Transacciones
    @FXML
    private BorderPane bpnTransMain;    
    @FXML
    private StackPane stpCenter;
    @FXML
    private Label regTransBienvenido;
    
    //Registrar transacción: Cedula
    @FXML
    private AnchorPane regTransCedula;
    @FXML
    private Label lblClienteEncontrado;
    @FXML
    private Label lblNoSeEncontroCliente;
    @FXML
    private TextField txfRegTransCedula;    

    
    
    
    //Registrar transacción: Tipo de transacción
    @FXML
    private AnchorPane regTransTipo;
    @FXML
    private ComboBox<String> regTransCbxTipoTrans;
    
    
    //Registrar transacción: Simulación
    @FXML
    private AnchorPane regTransSimulacion;
    @FXML
    private TextField txfRegTransMonto;
    @FXML
    private TextField txfRegTransNumCuotas;
    @FXML
    private TextField txfRegTransTasa;
    @FXML
    private DatePicker dtpFechaIniciacion;
    @FXML
    private TableView<SimulacionCredito> tableSimulacion;
    @FXML
    private TableColumn<SimulacionCredito, Integer> tableSimulacionNoCuota;
    @FXML
    private TableColumn<SimulacionCredito, Double> tableSimulacionMens;
    @FXML
    private TableColumn<SimulacionCredito, LocalDate> tableSimulacionFPago;    
    ObservableList<SimulacionCredito> listaSimulacion;
    
    
    //Registrar transacción : Soportes - Garantias
    @FXML
    private AnchorPane regTransSoportes;     
    @FXML
    private Label lblRegTransClieSinGarantias;    
    @FXML
    private VBox vbxRegTransTablaGarantias;
    @FXML
    private TableView<Garantia> tableRegTransGarantias;
    @FXML
    private TableColumn<Garantia, Integer> tableRegTransGarantiasCod;
    @FXML
    private TableColumn<Garantia, String> tableRegTransGarantiasTipo;
    @FXML
    private TableColumn<Garantia, Double> tableRegTransGarantiasVal;
    @FXML
    private TableColumn<Garantia, String> tableRegTransGarantiasUbi;
    @FXML
    private HBox hbxGarantiasAñadidas;
    @FXML
    private TextArea taGarantiasAñadidas;
    ObservableList<Garantia> listaGarantias;
    ArrayList<Garantia> listaGarantiasAñadidas = new ArrayList<Garantia>();
    StringBuilder contenidotaGarantiasAñadidas = new StringBuilder("");
    
    //Registrar transacción : Soportes - Fiador  
    @FXML
    private AnchorPane regTransSoportesFiador; 
    @FXML
    private TextField txfRegTransFiador;
    
 
	@FXML    
    public void entrarHome(ActionEvent event) throws IOException {
    	Parent home = FXMLLoader.load(getClass().getResource("/vista/home.fxml"));
		Scene homeScene = new Scene(home);
		Window nodo = ((Node) event.getSource()).getScene().getWindow();
		Stage ventana = (Stage)(nodo);
		ventana.setScene(homeScene);
		ventana.show();
    }
    
    @FXML
    public void registrarTrans(ActionEvent event) throws IOException {    	
    	esconderPanesMenosIndicado(regTransCedula);
    } 
    
    
    @FXML
    public void regTransBuscarCliente() throws SQLException{
    	//Busca el cliente
    	clienteRegistro = controlGeneral.ejecutarSentencia("SELECT * from cliente WHERE cedula like '" + txfRegTransCedula.getText() +"';");
    	if(clienteRegistro.next()) {
    		cedulaCliente = clienteRegistro.getString("cedula");
    		cedulaOK=true;
    		lblNoSeEncontroCliente.setVisible(false);
    		lblClienteEncontrado.setVisible(true);    	
    		objTransaccion.setClienteTrans(cedulaCliente);
    	}
    	else {
    		cedulaOK=false;    		
    		lblClienteEncontrado.setVisible(false);
    		lblNoSeEncontroCliente.setVisible(true);
    	}
    }

    @FXML
    public void continuarATipoTransaccion(ActionEvent event) throws IOException {
    	if(cedulaOK) {
    		esconderPanesMenosIndicado(regTransTipo);
    	}
    	else
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Cédula incorrecta", "Cliente no asociado", "Por favor ingrese una cédula válida.");
    }
    
    @FXML
    public void continuarASimulacion(ActionEvent event) {
    	//Guarda el tipo de transacción en el objeto.
    	if(regTransCbxTipoTrans.getValue().equals("Inversión"))
			objTransaccion.setTipoTrans('I');
		else
			objTransaccion.setTipoTrans('P');
    	
    	esconderPanesMenosIndicado(regTransSimulacion);
    }
    
    @FXML
    public void generarSimulacion(ActionEvent event) {
    	
    	if(txfRegTransMonto.getText().isEmpty() || txfRegTransNumCuotas.getText().isEmpty()
    			|| txfRegTransTasa.getText().isEmpty() |dtpFechaIniciacion.getValue() == null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Campos vacíos", "Por favor rellene todos los campos", null);
    		return;
    	}
    	
    	//Fórmula de la anualidad para calcular la mensualidad.
    	this.monto = Double.parseDouble(txfRegTransMonto.getText());
    	this.numCuotas = Integer.parseInt(txfRegTransNumCuotas.getText());
    	this.tasaEA = Double.parseDouble(txfRegTransTasa.getText());
    	
    	//Validación numCuotas
    	if(numCuotas>120) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Error", "Número de cuotas inválido","Máximo 120 cuotas");
    		return;
    	}
    	
    	double tasaMensual = (Math.pow(tasaEA+1, 1.0/12.0)) - 1.0;
    	this.mensualidad= (tasaMensual*monto)/(1.0 - (Math.pow((1+tasaMensual),-numCuotas)));        	
    	
    	this.fechaIniciacion = dtpFechaIniciacion.getValue();        	
    	
    	//Table view simulación
    	listaSimulacion = FXCollections.observableArrayList();
    	tableSimulacionNoCuota.setCellValueFactory(new PropertyValueFactory<>("noCuota"));
    	tableSimulacionMens.setCellValueFactory(new PropertyValueFactory<>("mensualidad"));
    	tableSimulacionFPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
    	
    	for(int i=0; i<numCuotas;i++) {
    		int noCuota = i+1;
    		LocalDate fechaPago = fechaIniciacion.plusMonths(i+1);
    		SimulacionCredito simulacionCuota = new SimulacionCredito(noCuota, mensualidad, fechaPago);
    		listaSimulacion.add(simulacionCuota);
    	}
    	
    	tableSimulacion.setItems(listaSimulacion);
    	
    }
    
    
    @FXML
    public void continuarASoportes(ActionEvent event) throws SQLException {
    	
    	//Validación campos vacíos.
    	if(txfRegTransMonto.getText().isEmpty() || txfRegTransNumCuotas.getText().isEmpty()
    			|| txfRegTransTasa.getText().isEmpty() |dtpFechaIniciacion.getValue() == null) {
    		controlGeneral.mostrarAlertaSinContent(AlertType.ERROR, "Campos vacíos", "Por favor rellene todos los campos");
    		return;	
    	}
    	//Se muestra la simulación de crédito según los datos introducidos.
    	//A partir de este método se obtiene la mensualidad de las cuotas.
    	generarSimulacion(event);
    	
    	//Por defecto, el usuario no tiene garantías entonces se esconden los Panes y se muestra el label de no garantías.
    	vbxRegTransTablaGarantias.setVisible(false);
		taGarantiasAñadidas.setVisible(false);
		hbxGarantiasAñadidas.setVisible(false);
		lblRegTransClieSinGarantias.setVisible(true);
		
    	//Consultar si el cliente asociado tiene garantías.
    	String secuenciaSQL = "SELECT  garantias.* FROM garantias JOIN cliente ON clientegarantia=cedula WHERE cedula like '"
    	+ objTransaccion.getClienteTrans()+"';";
    	ResultSet garantias = controlGeneral.ejecutarSentencia(secuenciaSQL);
    	
    	//Si es un préstamo, muestreme el pane de Soportes.
    	if(objTransaccion.getTipoTrans()=='P') {
    		
    		//Si el cliente tiene garantías se definen las columnas en las tablas. Después, se muestran los resultados.
    		try {
	    		if(garantias.next()) {
	    			//Los panes de la información de las garantías se vuelven visibles.
	    			vbxRegTransTablaGarantias.setVisible(true);
	    	    	taGarantiasAñadidas.setVisible(true);
	    			lblRegTransClieSinGarantias.setVisible(false);
	    			hbxGarantiasAñadidas.setVisible(true);
	    			
	    			//Se definen las columnas de la tabla.
	    			tableRegTransGarantiasCod.setCellValueFactory(new PropertyValueFactory<>("codGarantia"));
	    			tableRegTransGarantiasTipo.setCellValueFactory(new PropertyValueFactory<>("tipoGarantia"));
	    			tableRegTransGarantiasVal.setCellValueFactory(new PropertyValueFactory<>("valorGarantia"));
	    			tableRegTransGarantiasUbi.setCellValueFactory(new PropertyValueFactory<>("ubiGarantia"));    			    		
	    			
	    			//Buscar garantías retorna un ObservableList<> con las garantías del usuario.
	    			listaGarantias = buscarGarantias(objTransaccion.getClienteTrans());
	    			//Se agrega esa lista de garantías a la tabla.
	    			tableRegTransGarantias.setItems(listaGarantias);
        	}
    		}catch(Exception e) {
    			System.out.println(e.getLocalizedMessage());
    		}
    		
    			
    		//Muestra el pane de Soportes.
    		esconderPanesMenosIndicado(regTransSoportes);
    	}
    	
    	//Agregar valores al objeto transacción.
    	objTransaccion.setMontoTrans(monto);
    	objTransaccion.setTasaTrans(tasaEA);
    	objTransaccion.setNumCuotas(numCuotas);
    	objTransaccion.setFechaIniciacion(fechaIniciacion);
    	//La fecha de término se cumple cuando se pagan todas las cuotas.
    	fechaTermino = fechaIniciacion.plusMonths(numCuotas);
    	objTransaccion.setFechaTermino(fechaTermino);
    	objTransaccion.setFechaSolicitud(LocalDate.now());
    	objTransaccion.setEstadoSolicitud("INCOMPLETA");   

    }
    
    
    public ObservableList<Garantia> buscarGarantias(String clienteGarantiaRS) throws SQLException{
    	
    	//Se crea la lista de garantías.
    	ObservableList<Garantia> listaGar = FXCollections.observableArrayList();
    	//Se hace la consulta en la base de datos.
    	String secuenciaSQL = "SELECT  garantias.* FROM garantias JOIN cliente ON clientegarantia=cedula WHERE cedula like '"
    	    	+ clienteGarantiaRS +"';";
    	ResultSet garantiasRS = controlGeneral.ejecutarSentencia(secuenciaSQL);
    	
    	//Si se encontraron resultados, cree un objeto Garantía y agreguelo a la lista.
    	while(garantiasRS.next()) {
    	Garantia objGarantiaRS = new Garantia(garantiasRS.getInt("codGarantia"),garantiasRS.getString("clienteGarantia"),garantiasRS.getString("tipoGarantia"),
    			garantiasRS.getString("valorGarantia"),garantiasRS.getString("ubiGarantia"));
    	listaGar.add(objGarantiaRS);
    	}
    	return listaGar;
    }
    

    @FXML
    void añadirGarantiaRegTrans(ActionEvent event) {
    	
    	Garantia garantiaAñadida = new Garantia();
    	garantiaAñadida = tableRegTransGarantias.getSelectionModel().getSelectedItem();
    	for(Garantia gar:listaGarantiasAñadidas) {
    		if(gar.equals(garantiaAñadida))
    			return;
    	}
    	listaGarantiasAñadidas.add(garantiaAñadida);
    	taGarantiasAñadidas.appendText("\n"+garantiaAñadida.toString());
    }

    @FXML
    void cancelarAñadidasRegTrans(ActionEvent event) {
    	listaGarantiasAñadidas = new ArrayList<>();
    	taGarantiasAñadidas.clear(); 	
    }
    
    @FXML
	public void validarInputNumerico(KeyEvent event) {
		try {
			TextField textfield = (TextField) event.getSource();
			textfield.setTextFormatter(new TextFormatter<>(change ->
	        (change.getControlNewText().matches("[0-9]{0,13}(\\.[0-9]*)?")) ? change : null));
			}catch(Exception e) {
				e.getStackTrace();
		}
	}
	
	@FXML
	public void validarInputEntero(KeyEvent event) {
		try {
			TextField textfield = (TextField) event.getSource();
			textfield.setTextFormatter(new TextFormatter<>(change ->
	        (change.getControlNewText().matches("^[0-9]{0,3}$")) ? change : null));
			}catch(Exception e) {
				e.getStackTrace();
		}
	}
	
	
	public void esconderPanesMenosIndicado(Node nodo) {
		
		ObservableList<Node> hijos = stpCenter.getChildren();
		for(Node hijo : hijos) {
			hijo.setVisible(false);
		}
		nodo.setVisible(true);
	}
	
	@FXML
	public void botonAtras(ActionEvent event) {
		Node nodo = (Node) event.getSource();
		Parent actual = nodo.getParent();
		ObservableList<Node> hijos = stpCenter.getChildren();
		for(int i=0; i<hijos.size();i++) {
			if(hijos.get(i).equals(actual))
				esconderPanesMenosIndicado(hijos.get(i-1));
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		esconderPanesMenosIndicado(regTransBienvenido);	
		//Se inicia las opciones del ComboBox TipoTransaccion y se selecciona la primera.
		regTransCbxTipoTrans.getItems().addAll("Préstamo","Inversión");
		regTransCbxTipoTrans.getSelectionModel().selectFirst();
		//Se esconden los VBox que contienen las info de garantias. La tabla y las que serán añadidas.
		vbxRegTransTablaGarantias.setVisible(false);
		taGarantiasAñadidas.setVisible(false);
		hbxGarantiasAñadidas.setVisible(false);
		//En la casilla fechaIniciacion se pone por defecto la hora del sistema.
		dtpFechaIniciacion.setValue(LocalDate.now());
	}

}