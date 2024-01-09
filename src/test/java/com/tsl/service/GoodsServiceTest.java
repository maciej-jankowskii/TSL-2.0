package com.tsl.service;

import com.tsl.dtos.GoodsDTO;
import com.tsl.exceptions.CannotEditGoodsAssignedToOrderException;
import com.tsl.exceptions.GoodsNotFoundException;
import com.tsl.exceptions.NonUniqueLabelsException;
import com.tsl.mapper.GoodsMapper;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.repository.GoodsRepository;
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

class GoodsServiceTest {

    @Mock private GoodsRepository goodsRepository;
    @Mock private GoodsMapper goodsMapper;
    @InjectMocks private GoodsService goodsService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Goods")
    public void testFindAll_Success(){
        Goods goods1 = prepareFirstGoods();
        Goods goods2 = prepareSecondGoods();
        GoodsDTO goodsDTO1 = prepareFirstGoodsDTO();
        GoodsDTO goodsDTO2 = prepareSecondGoodsDTO();
        List<Goods> goodsList = Arrays.asList(goods1, goods2);

        when(goodsRepository.findAll()).thenReturn(goodsList);
        when(goodsMapper.mapToDTO(goods1)).thenReturn(goodsDTO1);
        when(goodsMapper.mapToDTO(goods2)).thenReturn(goodsDTO2);
        List<GoodsDTO> resultGoods = goodsService.findAll();

        assertNotNull(resultGoods);
        assertEquals(resultGoods.size(), goodsList.size());
        verify(goodsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find all Goods not assigned to order")
    public void testFindAllNotAssignedToOrderGoods_Success(){
        Goods goods1 = prepareFirstGoods();
        Goods goods2 = prepareSecondGoods();
        GoodsDTO goodsDTO1 = prepareFirstGoodsDTO();
        GoodsDTO goodsDTO2 = prepareSecondGoodsDTO();
        List<Goods> goodsList = Arrays.asList(goods1, goods2);

        when(goodsRepository.findAllByAssignedToOrderIsFalse()).thenReturn(goodsList);
        when(goodsMapper.mapToDTO(goods1)).thenReturn(goodsDTO1);
        when(goodsMapper.mapToDTO(goods2)).thenReturn(goodsDTO2);
        List<GoodsDTO> resultGoods = goodsService.findAllNotAssignedToOrderGoods();

        assertNotNull(resultGoods);
        assertEquals(resultGoods.size(), goodsList.size());
        verify(goodsRepository, times(1)).findAllByAssignedToOrderIsFalse();
    }

    @Test
    @DisplayName("Should find Goods by ID")
    public void testFindGoodsById_Success(){
        Goods goods = prepareFirstGoods();
        GoodsDTO goodsDTO = prepareFirstGoodsDTO();

        when(goodsRepository.findById(goods.getId())).thenReturn(Optional.of(goods));
        when(goodsMapper.mapToDTO(goods)).thenReturn(goodsDTO);

        GoodsDTO resultGoods = goodsService.findGoodsById(goods.getId());

        assertEquals(resultGoods, goodsDTO);
        verify(goodsRepository, times(1)).findById(goods.getId());
    }

    @Test
    @DisplayName("Should throw GoodsNotFoundException")
    public void testFindGoodsById_GoodsNotFound(){
        Long goodsId = 1L;

        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.findGoodsById(goodsId));
    }

    @Test
    @DisplayName("Should add new Goods successfully")
    public void testAddGoods_Success(){
        Goods goods = prepareFirstGoods();
        GoodsDTO goodsDTO = prepareFirstGoodsDTO();
        goodsDTO.setAssignedToOrder(false);

        when(goodsMapper.mapToEntity(goodsDTO)).thenReturn(goods);
        when(goodsRepository.existsByLabelAndAssignedToOrderFalse(goods.getLabel())).thenReturn(false);
        when(goodsRepository.save(goods)).thenReturn(goods);
        when(goodsMapper.mapToDTO(goods)).thenReturn(goodsDTO);

        GoodsDTO result = goodsService.addGoods(goodsDTO);

        verify(goodsRepository, times(1)).save(any());
        assertEquals("Goods A", result.getName());
        assertFalse(result.getAssignedToOrder());
    }

    @Test
    @DisplayName("Should throw NonUniqueLabelsException when adding Goods with non-unique label")
    public void testAddGoods_NonUniqueLabel() {
        Goods goods = prepareFirstGoods();
        GoodsDTO goodsDTO = prepareFirstGoodsDTO();

        when(goodsMapper.mapToEntity(goodsDTO)).thenReturn(goods);
        when(goodsRepository.existsByLabelAndAssignedToOrderFalse(goods.getLabel())).thenReturn(true);

        assertThrows(NonUniqueLabelsException.class, () -> goodsService.addGoods(goodsDTO));

        verify(goodsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Goods successfully")
    public void testUpdateGoods_Success(){
        GoodsDTO currentGoods = prepareFirstGoodsDTO();
        GoodsDTO updatedGoodsDTO = prepareSecondGoodsDTO();
        Goods goods = prepareSecondGoods();

        when(goodsMapper.mapToEntity(updatedGoodsDTO)).thenReturn(goods);
        when(goodsRepository.findById(updatedGoodsDTO.getId())).thenReturn(Optional.of(goods));
        when(goodsRepository.save(goods)).thenReturn(goods);

        assertDoesNotThrow(() -> goodsService.updateGoods(currentGoods, updatedGoodsDTO));

        verify(goodsRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw CannotEditGoodsAssignedToOrderException when updating Goods assigned to order")
    public void testUpdateGoods_AssignedToOrder() {
        GoodsDTO currentGoods = prepareFirstGoodsDTO();
        currentGoods.setAssignedToOrder(true);
        GoodsDTO updatedGoodsDTO = prepareSecondGoodsDTO();
        Goods goods = prepareFirstGoods();

        when(goodsMapper.mapToEntity(updatedGoodsDTO)).thenReturn(goods);
        when(goodsRepository.findById(updatedGoodsDTO.getId())).thenReturn(Optional.of(goods));

        assertThrows(CannotEditGoodsAssignedToOrderException.class, () -> goodsService.updateGoods(currentGoods, updatedGoodsDTO));

        verify(goodsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CannotEditGoodsAssignedToOrderException when changing assignedToOrder from true to false")
    public void testUpdateGoods_ChangeAssignedToOrder() {
        GoodsDTO currentGoods = prepareFirstGoodsDTO();
        GoodsDTO updatedGoodsDTO = prepareFirstGoodsDTO();
        Goods goods = prepareFirstGoods();
        currentGoods.setAssignedToOrder(true);
        updatedGoodsDTO.setAssignedToOrder(false);

        when(goodsMapper.mapToEntity(updatedGoodsDTO)).thenReturn(goods);
        when(goodsRepository.findById(currentGoods.getId())).thenReturn(Optional.of(goods));

        assertThrows(CannotEditGoodsAssignedToOrderException.class, () -> goodsService.updateGoods(currentGoods, updatedGoodsDTO));

        verify(goodsRepository, never()).save(any());
    }




    private GoodsDTO prepareFirstGoodsDTO() {
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(1L);
        goodsDTO.setLabel("ABC");
        goodsDTO.setName("Goods A");
        goodsDTO.setAssignedToOrder(false);
        return goodsDTO;
    }

    private GoodsDTO prepareSecondGoodsDTO() {
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setId(2L);
        goodsDTO.setName("Goods B");
        goodsDTO.setAssignedToOrder(false);
        return goodsDTO;
    }

    private Goods prepareSecondGoods() {
        Goods goods = new Goods();
        goods.setId(2L);
        goods.setName("Goods B");
        goods.setAssignedToOrder(false);
        return goods;
    }

    private static Goods prepareFirstGoods() {
        Goods goods = new Goods();
        goods.setId(1L);
        goods.setLabel("ABC");
        goods.setName("Goods A");
        goods.setAssignedToOrder(false);
        return goods;
    }





}