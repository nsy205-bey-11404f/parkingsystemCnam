package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    IUserService userService;

    public FareCalculatorService() {
        this(new UserService(new TicketDAO()));
    }

    public FareCalculatorService(IUserService userService) {
        this.userService = userService;
    }

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMinutes = ticket.getInTime().getTime()/1000/60;
        long outMinutes = ticket.getOutTime().getTime()/1000/60;

        long durationm = outMinutes - inMinutes;
  
        double rate = 1.0;
    
        int duration = (int) durationm / 60; 
        if (durationm <= 30) { // Stationnement gratuit pour les 30 premières minutes
            rate = 0.0;
        } else if (durationm < 60) {
            rate = 0.75;
            duration = 1;
        }
       
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(rate * duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(rate * duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    
        double price = ticket.getPrice();
        if (price == 0) {
            return;
        }

        boolean isRecurrentUser = userService.isRecurrentUser(ticket.getVehicleRegNumber());
        if (isRecurrentUser) {
            ticket.setPrice(price - (0.05 * price));
        }
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
}