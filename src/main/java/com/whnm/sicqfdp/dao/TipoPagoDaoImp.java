/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whnm.sicqfdp.dao;

import com.whnm.sicqfdp.beans.CustomUser;
import com.whnm.sicqfdp.beans.ListTipoPago;
import com.whnm.sicqfdp.beans.Parametros;
import com.whnm.sicqfdp.beans.TipoPago;
import com.whnm.sicqfdp.interfaces.TipoPagoDao;
import com.whnm.sicqfdp.spbeans.SpListarTipoEquipo;
import com.whnm.sicqfdp.spbeans.SpListarTipoPago;
import com.whnm.sicqfdp.spbeans.SpMantTipoPago;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author wilson
 */
@Service(value="tipoPagoDao")
public class TipoPagoDaoImp implements TipoPagoDao{
    private DataSource dataSource;
    private SpListarTipoPago spListarTipoPago;
    private SpMantTipoPago spMantTipoPago;
    
    @Autowired
    public TipoPagoDaoImp(@Qualifier("dataSource1") DataSource dataSource) {
        this.dataSource = dataSource;
        this.spListarTipoPago = new SpListarTipoPago(dataSource);
        this.spMantTipoPago = new SpMantTipoPago(dataSource);
    }
    
    @Override
    public TipoPago mantenimientoTipoPago(Integer opcCrud, TipoPago tipoPa, CustomUser user) {
        TipoPago tipoPago = new TipoPago();
        Map<String,Object> vars = new HashMap<String,Object>();
        vars.put(SpMantTipoPago.PARAM_IN_IDTIPOOPERACION, opcCrud);
        vars.put(SpMantTipoPago.PARAM_IN_IDPAGO, tipoPa.getId());
        vars.put(SpMantTipoPago.PARAM_IN_CONCEPTO_PARA, tipoPa.getConceptoPara());
        vars.put(SpMantTipoPago.PARAM_IN_DENOMINACION, tipoPa.getConcepto());
        vars.put(SpMantTipoPago.PARAM_IN_ESTADO, tipoPa.getEstado());
        vars.put(SpMantTipoPago.PARAM_IN_EST_COLEG, tipoPa.getEstadosColegiados());
        vars.put(SpMantTipoPago.PARAM_IN_ES_INHABILITADORA, tipoPa.getEsInhabilitadora());
        if(opcCrud == 1){
            vars.put(SpMantTipoPago.PARAM_IN_FECHA_VIG, tipoPa.getPrecioActual().getFechaInicioVigencia());
            vars.put(SpMantTipoPago.PARAM_IN_PRECIO, tipoPa.getPrecioActual().getPrecio());
        } else {
            vars.put(SpMantTipoPago.PARAM_IN_FECHA_VIG, " ");
            vars.put(SpMantTipoPago.PARAM_IN_PRECIO, 0);
        }
        vars.put(SpMantTipoPago.PARAM_IN_NUMERO_ACTIVAS, tipoPa.getNumPagosActivos());
        vars.put(SpMantTipoPago.PARAM_IN_TIPO_GENERACION, tipoPa.getTipoGeneracion());
        vars.put(SpMantTipoPago.PARAM_IN_USUARIO, user.getUsername());
        try {
            Map<String, Object> result = (Map<String, Object>) spMantTipoPago.execute(vars);
            tipoPago.setIndError(Integer.parseInt(String.valueOf(result.get(Parametros.IND))));
            tipoPago.setMsjError(result.get(Parametros.MSJ).toString());        
        } catch(Exception ex){
            Logger.getLogger(TipoPagoDaoImp.class.getName()).log(Level.SEVERE, null, ex);
            tipoPago.setIndError(1);
            tipoPago.setMsjError("Error: ["+ex.getMessage()+"]");        
        }
        return tipoPago;
    }

    @Override
    public ListTipoPago listarTipoPago(Integer opcListado, TipoPago tipoPa) {
        ListTipoPago listaTiposPagos = new ListTipoPago();
        List<TipoPago> tiposPagos = new ArrayList<>();
        Map<String,Object> vars = new HashMap<String,Object>();
        vars.put(SpListarTipoPago.PARAM_IN_IDTIPOOPERACION, opcListado);
        vars.put(SpListarTipoPago.PARAM_IN_CONCEPTO_PARA, tipoPa.getConceptoPara());
        vars.put(SpListarTipoPago.PARAM_IN_ESTADO, tipoPa.getEstado());
        vars.put(SpListarTipoPago.PARAM_IN_IDPAGO, tipoPa.getId());
        try {
            Map<String, Object> result = (Map<String, Object>) spListarTipoPago.execute(vars);
            List<Map<String, Object>> listResult = (List<Map<String, Object>>) result.get(Parametros.RESULSET);
            listaTiposPagos.setIndError(Integer.parseInt(String.valueOf(result.get(Parametros.IND))));
            listaTiposPagos.setMsjError(result.get(Parametros.MSJ).toString());
            for (Map<String, Object> item : listResult) {
                TipoPago tPagos = new TipoPago();
                tPagos.setId(item.get("id_pago") != null ? Long.parseLong(String.valueOf(item.get("id_pago"))) : -1);
                tPagos.setConcepto(item.get("denominacion") != null ? String.valueOf(item.get("denominacion")) : "");
                tPagos.setEsInhabilitadora(item.get("es_inhabilitador") != null ? Integer.parseInt(String.valueOf(item.get("es_inhabilitador"))) : -1);
                tPagos.setEstado(item.get("habilitado") != null ? Integer.parseInt(String.valueOf(item.get("habilitado"))) : -1);
                tPagos.setNumPagosActivos(item.get("numero_activas") != null ? Integer.parseInt(String.valueOf(item.get("numero_activas"))) : -1);
                tPagos.setConceptoPara(item.get("concepto_para") != null ? String.valueOf(item.get("concepto_para")) : "");
                tPagos.setEstadosColegiados(item.get("estados_colegiados") != null ? String.valueOf(item.get("estados_colegiados")) : "");
                tPagos.setTipoGeneracion(item.get("tipo_generacion") != null ? Integer.parseInt(String.valueOf(item.get("tipo_generacion"))) : -1);
                tiposPagos.add(tPagos);
            }
            listaTiposPagos.setListTipoPago(tiposPagos);
        } catch(Exception ex){
            Logger.getLogger(TipoPagoDaoImp.class.getName()).log(Level.SEVERE, null, ex);
            listaTiposPagos.setIndError(1);
            listaTiposPagos.setMsjError("Error: ["+ex.getMessage()+"]");
        }
        return listaTiposPagos;
    }

    @Override
    public TipoPago grabar(TipoPago elemento, CustomUser user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TipoPago editar(TipoPago elemento, CustomUser user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TipoPago eliminar(TipoPago elemento, CustomUser user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TipoPago listar(TipoPago elemento) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}