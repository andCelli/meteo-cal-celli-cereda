/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

/**
 * This is an exception raised when it is impossible to download a forecast
 *
 * @author stefano
 */
class UnableToDownloadException extends Exception {

    public UnableToDownloadException(Throwable e) {
        super(e);
    }
    
    public UnableToDownloadException(String msg){
        super(msg);
    }
    
    public UnableToDownloadException(String msg, Throwable e){
        super(msg, e);
    }

}
