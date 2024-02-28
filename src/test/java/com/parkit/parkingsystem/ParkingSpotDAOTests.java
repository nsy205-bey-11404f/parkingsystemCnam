package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("ParkingSpotDAOTests")
@DisplayName("Unit tests for ParkingSpotDAO class")
public class ParkingSpotDAOTests {

    @InjectMocks
    private ParkingSpotDAO parkingSpotDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private ParkingSpot parkingSpot;
    private final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeEach
    public void setUpPerTest() {
        try {

            parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(1L));
            ticket.setOutTime(new Date(1L));
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
    @Tag("methodGetNextAvailableSlotTests")
    @DisplayName("Tests for method getNextAvailableSlot in ParkingSpotDAO class")
    public class GetNextAvailableSlotTests {

        @Test
        public void get_NextAvailableSlot_Should_ReturnNextAvailableSlot_When_ConnectionIsEstablishedAndPreparedStatementIsReturningResultSet()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt(1)).thenReturn(1);

            //act
            int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

            //assert
            Assertions.assertEquals(1, result);
            verify(dataBaseConfig, times(1)).getConnection();
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "CAR");
            verify(connection, times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_NextAvailableSlot_Should_ReturnMinusOne_When_ConnectionIsNotEstablished()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenThrow(ClassNotFoundException.class);

            //act & assert
            Assertions.assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(dataBaseConfig, times(1)).closePreparedStatement(null);
            verify(dataBaseConfig, times(1)).closeResultSet(null);
            verify(dataBaseConfig, times(1)).closeConnection(null);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_NextAvailableSlot_Should_ReturnMinusOne_When_ExecuteQueryDoNotReturnResultSet()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "CAR");
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(null);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_NextAvailableSlot_Should_ReturnMinusOne_When_PreparedStatementIsNotAcquired()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(dataBaseConfig, times(1)).closeResultSet(null);
            verify(connection, times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(null);
            verify(dataBaseConfig, times(1)).closeResultSet(null);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_NextAvailableSlot_Should_ReturnMinusOne_When_ResultSetNextThrowException()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "CAR");
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }

        @Test
        public void get_NextAvailableSlot_Should_ReturnMinusOne_When_ResultSetNextReturnsFalse()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            //act & assert
            Assertions.assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            verify(preparedStatement, times(1)).executeQuery();
            verify(preparedStatement, times(1)).setString(1, "CAR");
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verify(dataBaseConfig, times(1)).closeResultSet(resultSet);
            verifyNoMoreInteractions(dataBaseConfig, connection, preparedStatement);
        }
    }

    @Nested
    @Tag("methodUpdateParkingTests")
    @DisplayName("Tests for method updateParking in ParkingSpotDAO class")
    public class UpdateParkingTests {

        @Test
        public void update_Parking_Should_UpdateParking_When_ConnectionIsEstablishedAndPreparedStatementIsAcquired()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            //act & assert
            Assertions.assertTrue(parkingSpotDAO.updateParking(parkingSpot));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void update_Parking_Should_ReturnFalse_When_ConnectionIsNotEstablished()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenThrow(ClassNotFoundException.class);

            //act & assert
            Assertions.assertFalse(parkingSpotDAO.updateParking(parkingSpot));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(dataBaseConfig, times(1)).closeConnection(null);
            verify(dataBaseConfig, times(1)).closePreparedStatement(null);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void update_Parking_Should_ReturnFalse_When_PreparedStatementIsNotAcquired() throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertFalse(parkingSpotDAO.updateParking(parkingSpot));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(null);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void update_Parking_Should_NotUpdateParking_When_ExecuteDoNotReturnResultSet()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);

            //act & assert
            Assertions.assertFalse(parkingSpotDAO.updateParking(parkingSpot));            
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
        
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }

        @Test
        public void update_Parking_Should_ThrowException_When_ExecuteThrowException()
                throws SQLException, ClassNotFoundException {

            //arrange
            when(dataBaseConfig.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenThrow(SQLException.class);

            //act & assert
            Assertions.assertFalse(parkingSpotDAO.updateParking(parkingSpot));
            verify(dataBaseConfig, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            verify(dataBaseConfig, times(1)).closeConnection(connection);
            verify(dataBaseConfig, times(1)).closePreparedStatement(preparedStatement);
            verifyNoMoreInteractions(dataBaseConfig, connection);
        }
    }
}