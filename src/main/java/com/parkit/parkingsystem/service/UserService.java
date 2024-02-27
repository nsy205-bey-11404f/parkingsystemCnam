package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;

public class UserService implements IUserService {
    
    private TicketDAO ticketDAO;

    public UserService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }
    
    @Override
    public boolean isRecurrentUser(String vehicleNumber) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            return false;
        }
        
        return ticketDAO.countTickets(vehicleNumber) > 1;
    }

}
