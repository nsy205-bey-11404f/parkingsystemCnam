package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("TicketDAOTests")
@DisplayName("Unit tests for TicketDAO class")
public class TicketDAOTests {

    @InjectMocks
    private TicketDAO ticketDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    ResultSet resultSet;

    @Mock
    Timestamp timestamp;

    private Ticket ticket;
    private final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeEach
    public void setUpPerTest() {
        try {

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket = new Ticket();
            ticket.setInTime(new Date(100L));
            ticket.setOutTime(new Date(100L));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setPrice(0.0);
            ticket.setId(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @AfterEach
    public void tearDown() {
        dataBaseTestConfig.closePreparedStatement(preparedStatement);
        dataBaseTestConfig.closeResultSet(resultSet);
        dataBaseTestConfig.closeConnection(connection);
    }

    @Nested
    @Tag("methodSaveTicketTests")
    @DisplayName("Tests for method saveTicket in TicketDAO class")
    public class SaveTicketTests {

        @Test
        public void save_Ticket_Should_SaveTicket_Entry_When_ConnectionIsEstablishedAndPreparedStatementIsAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            ticket.setOutTime(null);
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            //act & assert
            Assertions.assertTrue(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.SAVE_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void save_Ticket_Should_SaveTicket_Exit_When_ConnectionIsEstablishedAndPreparedStatementIsAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            //act & assert
            Assertions.assertTrue(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.SAVE_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void save_Ticket_Should_Return_False_When_ConnectionIsNotEstablished() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenThrow(ClassNotFoundException.class);

            //act & assert
            assertFalse(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
        }

        @Test
        public void save_Ticket_Should_Return_False_When_PreparedStatementIsNotAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenThrow(SQLException.class);

            //act & assert
            assertFalse(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.SAVE_TICKET);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
        }

        @Test
        public void save_Ticket_Should_NotSaveTicket_When_ExecuteUpdateReturnZeroRows() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            //act & assert
            Assertions.assertFalse(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.SAVE_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void save_Ticket_Should_Return_False_When_ExecuteUpdateThrowException() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertFalse(ticketDAO.saveTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.SAVE_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }
    }

   @Nested
    @Tag("methodGetTicketTests")
    @DisplayName("Tests for method getTicket in TicketDAO class")
    public class GetTicketTests {

        @Test
        public void get_Ticket_Should_ReturnTicket_When_ConnectionIsEstablishedAndPreparedStatementIsReturningResultSet()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt(anyInt())).thenReturn(1);
            when(resultSet.getString(anyInt())).thenReturn("CAR");
            when(resultSet.getDouble(anyInt())).thenReturn(0.0);
            when(resultSet.getTimestamp(anyInt())).thenReturn(new Timestamp(100L));

            //act
            Ticket ticketNew = ticketDAO.getTicket("ABCDEF");

            //assert
            Assertions.assertEquals(ticket.getId(), ticketNew.getId());
            Assertions.assertEquals(ticket.getInTime(), ticketNew.getInTime());
            Assertions.assertEquals(ticket.getOutTime(), ticketNew.getOutTime());
            Assertions.assertEquals(ticket.getParkingSpot(), ticketNew.getParkingSpot());
            Assertions.assertEquals(ticket.getPrice(), ticketNew.getPrice());
            Assertions.assertEquals(ticket.getVehicleRegNumber(), ticketNew.getVehicleRegNumber());
            verify(dataBaseConfig, times(1)).getConnection();
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "ABCDEF");
            verify(connection, times(1)).prepareStatement(DBConstants.GET_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_Ticket_Should_ReturnNull_When_ConnectionIsNotEstablished()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenThrow(ClassNotFoundException.class);

            //act & assert
            Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
            verify(dataBaseConfig, times(1)).getConnection();
        }

        @Test
        public void get_Ticket_Should_ReturnNull_When_ExecuteQueryThrowException()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(dataBaseConfig, times(1)).closeResultSet(null);
            verify(connection, times(1)).prepareStatement(DBConstants.GET_TICKET);
            verify(preparedStatement, times(1)).executeQuery();
        }

        @Test
        public void get_Ticket_Should_ReturnNull_When_PreparedStatementIsNotAcquired()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_TICKET)).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
            verify(dataBaseConfig, times(1)).getConnection();
        }

        @Test
        public void get_Ticket_Should_ReturnNull_When_ResultSetNextThrowException()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.GET_TICKET);
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "ABCDEF");
        }

        @Test
        public void get_Ticket_Should_ReturnNull_When_ResultSetNextReturnFalse()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            //act & assert
            Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.GET_TICKET);
            verify(preparedStatement, times(1)).setString(1, "ABCDEF");            
            verify(preparedStatement, times(1)).executeQuery();
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }
    }

    @Nested
    @Tag("methodUpdateTicketTests")
    @DisplayName("Tests for method updateTicket in TicketDAO class")
    public class UpdateTicketTests {

        @Test
        public void update_Ticket_Should_UpdateTicket_When_ConnectionIsEstablishedAndPreparedStatementIsAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            //act & assert
            Assertions.assertTrue(ticketDAO.updateTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_TICKET);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void update_Ticket_Should_ReturnFalse_When_ConnectionIsNotEstablished()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenThrow(ClassNotFoundException.class);

            //act & assert
            Assertions.assertFalse(ticketDAO.updateTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
        }

        @Test
        public void update_Ticket_Should_ReturnFalse_When_PreparedStatementIsNotAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertFalse(ticketDAO.updateTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_TICKET);
        }

        @Test
        public void update_Ticket_Should_NotUpdateTicket_When_ExecuteUpdateReturnZeroRows() throws SQLException, ClassNotFoundException {
            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            //act & assert
            Assertions.assertFalse(ticketDAO.updateTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_TICKET);
        }

        @Test
        public void update_Ticket_Should_ReturnFalse_When_ExecuteUpdateThrowException() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertFalse(ticketDAO.updateTicket(ticket));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_TICKET);
        }
    }
}