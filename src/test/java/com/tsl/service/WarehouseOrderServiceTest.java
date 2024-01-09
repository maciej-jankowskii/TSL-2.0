package com.tsl.service;

import com.tsl.dtos.WarehouseOrderDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.IncompatibleGoodsTypeException;
import com.tsl.exceptions.WarehouseOrderIsAlreadyCompletedException;
import com.tsl.exceptions.WarehouseOrderNotFoundException;
import com.tsl.mapper.WarehouseOrderMapper;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.WarehouseOrderRepository;
import com.tsl.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseOrderServiceTest {

    @Mock private WarehouseOrderRepository warehouseOrderRepository;
    @Mock private WarehouseOrderMapper warehouseOrderMapper;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private StorageCostService storageCostService;
    @InjectMocks private WarehouseOrderService orderService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Warehouse Orders successfully")
    public void testFindAllWarehouseOrders_Success() {
        WarehouseOrder warehouseOrder1 = prepareFirstWarehouseOrder();
        WarehouseOrder warehouseOrder2 = prepareSecondWarehouseOrder();
        WarehouseOrderDTO warehouseOrderDTO1 = prepareFirstWarehouseOrderDTO();
        WarehouseOrderDTO warehouseOrderDTO2 = prepareSecondWarehouseOrderDTO();

        when(warehouseOrderRepository.findAll()).thenReturn(Arrays.asList(warehouseOrder1, warehouseOrder2));
        when(warehouseOrderMapper.mapToDTO(warehouseOrder1)).thenReturn(warehouseOrderDTO1);
        when(warehouseOrderMapper.mapToDTO(warehouseOrder2)).thenReturn(warehouseOrderDTO2);

        List<WarehouseOrderDTO> resultOrders = orderService.findAllWarehouseOrders();

        assertEquals(2, resultOrders.size());
        assertEquals(warehouseOrderDTO1, resultOrders.get(0));
        assertEquals(warehouseOrderDTO2, resultOrders.get(1));
        verify(warehouseOrderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Warehouse Order by ID successfully")
    public void testFindWarehouseOrder_Success() {
        Long orderId = 1L;
        WarehouseOrder warehouseOrder = prepareFirstWarehouseOrder();
        WarehouseOrderDTO warehouseOrderDTO = prepareFirstWarehouseOrderDTO();

        when(warehouseOrderRepository.findById(orderId)).thenReturn(Optional.of(warehouseOrder));
        when(warehouseOrderMapper.mapToDTO(warehouseOrder)).thenReturn(warehouseOrderDTO);

        WarehouseOrderDTO resultOrder = orderService.findWarehouseOrder(orderId);

        assertEquals(warehouseOrderDTO, resultOrder);
        verify(warehouseOrderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should throw WarehouseOrderNotFoundException when Warehouse Order not found by ID")
    public void testFindWarehouseOrder_NotFound() {
        Long orderId = 1L;

        when(warehouseOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(WarehouseOrderNotFoundException.class, () -> orderService.findWarehouseOrder(orderId));

        verify(warehouseOrderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should add Warehouse Order successfully")
    public void testAddWarehouseOrder_Success() {
        WarehouseOrderDTO warehouseOrderDTO = prepareFirstWarehouseOrderDTO();
        WarehouseOrder warehouseOrder = prepareFirstWarehouseOrder();
        Warehouse warehouse = prepareWarehouse();
        List<Goods> goodsList = prepareGoodsList();
        warehouseOrder.setGoods(goodsList);
        warehouseOrder.setWarehouse(warehouse);


        when(warehouseOrderMapper.mapToEntity(warehouseOrderDTO)).thenReturn(warehouseOrder);
        when(storageCostService.calculateStorageCosts(warehouseOrder)).thenReturn(150.0);
        when(warehouseOrderRepository.save(warehouseOrder)).thenReturn(warehouseOrder);
        when(warehouseOrderMapper.mapToDTO(warehouseOrder)).thenReturn(warehouseOrderDTO);


        WarehouseOrderDTO resultWarehouseOrder = orderService.addWarehouseOrder(warehouseOrderDTO);

        assertNotNull(resultWarehouseOrder);
        verify(warehouseRepository, times(1)).save(warehouse);
        verify(warehouseOrderRepository, times(1)).save(warehouseOrder);
    }

    @Test
    @DisplayName("Should throw IncompatibleGoodsTypeException")
    public void testAddWarehouseOrder_IncompatibleGoodsType() {
        WarehouseOrderDTO warehouseOrderDTO = prepareFirstWarehouseOrderDTO();
        WarehouseOrder warehouseOrder = prepareFirstWarehouseOrder();
        List<Goods> goodsList = prepareIncompatibleGoodsList();
        Warehouse warehouse = prepareWarehouse();
        warehouseOrder.setGoods(goodsList);


        when(warehouseOrderMapper.mapToEntity(warehouseOrderDTO)).thenReturn(warehouseOrder);

        assertThrows(IncompatibleGoodsTypeException.class, () -> orderService.addWarehouseOrder(warehouseOrderDTO));

        verify(warehouseRepository, never()).save(warehouse);
        verify(warehouseOrderRepository, never()).save(warehouseOrder);
    }

    @Test
    @DisplayName("Should mark Warehouse Order as completed successfully")
    public void testMarkWarehouseOrderAsCompleted_Success() {
        Long warehouseOrderId = 1L;
        WarehouseOrder warehouseOrder = prepareFirstWarehouseOrder();
        warehouseOrder.setIsCompleted(false);
        Warehouse warehouse = prepareWarehouse();
        warehouseOrder.setWarehouse(warehouse);

        when(warehouseOrderRepository.findById(warehouseOrderId)).thenReturn(java.util.Optional.of(warehouseOrder));

        assertDoesNotThrow(() -> orderService.markWarehouseOrderAsCompleted(warehouseOrder.getId()));

        assertTrue(warehouseOrder.getIsCompleted());
        verify(warehouseOrderRepository, times(1)).save(warehouseOrder);
    }

    @Test
    @DisplayName("Should throw WarehouseOrderNotFoundException")
    public void testMarkWarehouseOrderAsCompleted_WarehouseOrderNotFound() {
        Long warehouseOrderId = 1L;
        when(warehouseOrderRepository.findById(warehouseOrderId)).thenReturn(java.util.Optional.empty());

        assertThrows(WarehouseOrderNotFoundException.class, () -> orderService.markWarehouseOrderAsCompleted(warehouseOrderId));

        verify(warehouseOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw WarehouseOrderIsAlreadyCompletedException")
    public void testMarkWarehouseOrderAsCompleted_WarehouseOrderAlreadyCompleted() {
        Long warehouseOrderId = 1L;
        WarehouseOrder warehouseOrder = prepareSecondWarehouseOrder();
        warehouseOrder.setIsCompleted(true
        );
        when(warehouseOrderRepository.findById(warehouseOrderId)).thenReturn(java.util.Optional.of(warehouseOrder));

        assertThrows(WarehouseOrderIsAlreadyCompletedException.class, () -> orderService.markWarehouseOrderAsCompleted(warehouseOrderId));

        verify(warehouseOrderRepository, never()).save(any());
    }

    private List<Goods> prepareGoodsList() {
        Goods goods1 = new Goods();
        goods1.setId(1L);
        goods1.setTypeOfGoods(TypeOfGoods.REFRIGERATED_GOODS);
        goods1.setRequiredArea(100.0);

        Goods goods2 = new Goods();
        goods2.setId(2L);
        goods2.setTypeOfGoods(TypeOfGoods.REFRIGERATED_GOODS);
        goods2.setRequiredArea(200.0);

        return Arrays.asList(goods1, goods2);
    }

    private List<Goods> prepareIncompatibleGoodsList() {
        Goods goods1 = new Goods();
        goods1.setId(1L);
        goods1.setTypeOfGoods(TypeOfGoods.ADR_GOODS);
        goods1.setRequiredArea(100.0);

        Goods goods2 = new Goods();
        goods2.setId(2L);
        goods2.setTypeOfGoods(TypeOfGoods.BULK_GOODS);
        goods2.setRequiredArea(200.0);

        return Arrays.asList(goods1, goods2);
    }

    private Warehouse prepareWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAvailableArea(1000.0);
        warehouse.setTypeOfGoods(TypeOfGoods.REFRIGERATED_GOODS);
        return warehouse;
    }

    private WarehouseOrderDTO prepareFirstWarehouseOrderDTO() {
        WarehouseOrderDTO warehouseOrderDTO = new WarehouseOrderDTO();
        warehouseOrderDTO.setId(1L);
        return warehouseOrderDTO;
    }

    private WarehouseOrderDTO prepareSecondWarehouseOrderDTO() {
        WarehouseOrderDTO warehouseOrderDTO = new WarehouseOrderDTO();
        warehouseOrderDTO.setId(2L);
        return warehouseOrderDTO;
    }

    private static WarehouseOrder prepareSecondWarehouseOrder() {
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setId(2L);
        return warehouseOrder;
    }

    private static WarehouseOrder prepareFirstWarehouseOrder() {
        WarehouseOrder warehouseOrder = new WarehouseOrder();
        warehouseOrder.setId(1L);
        return warehouseOrder;
    }



}