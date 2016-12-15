package com.tcontrol.sensormonitor;

import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.impl.MySqlJDBCDaoImpl;
import java.io.Serializable;

//@ApplicationScoped
public class DAOFactory implements Serializable {

    public static DaoInterface dao;

    //@Produces 
    public static DaoInterface getInstance() {
        if (dao == null) {
            dao = new MySqlJDBCDaoImpl();
        }
        return dao;
    }
}
