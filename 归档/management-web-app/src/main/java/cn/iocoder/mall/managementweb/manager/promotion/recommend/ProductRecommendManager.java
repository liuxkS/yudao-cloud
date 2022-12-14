package cn.iocoder.mall.managementweb.manager.promotion.recommend;

import cn.iocoder.common.framework.util.CollectionUtils;
import cn.iocoder.common.framework.vo.CommonResult;
import cn.iocoder.common.framework.vo.PageResult;
import cn.iocoder.mall.managementweb.controller.promotion.recommend.vo.ProductRecommendCreateReqVO;
import cn.iocoder.mall.managementweb.controller.promotion.recommend.vo.ProductRecommendDetailVO;
import cn.iocoder.mall.managementweb.controller.promotion.recommend.vo.ProductRecommendPageReqVO;
import cn.iocoder.mall.managementweb.controller.promotion.recommend.vo.ProductRecommendUpdateReqVO;
import cn.iocoder.mall.managementweb.convert.promotion.ProductRecommendConvert;
import cn.iocoder.mall.productservice.rpc.spu.ProductSpuFeign;
import cn.iocoder.mall.productservice.rpc.spu.dto.ProductSpuRespDTO;
import cn.iocoder.mall.promotion.api.rpc.recommend.ProductRecommendFeign;
import cn.iocoder.mall.promotion.api.rpc.recommend.dto.ProductRecommendRespDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 商品推荐 Manager
 */
@Service
@Validated
public class ProductRecommendManager {

    @Autowired
    private ProductRecommendFeign productRecommendFeign;
    @Autowired
    private ProductSpuFeign productSpuFeign;
    /**
     * 创建商品推荐
     *
     * @param createVO 创建商品推荐 VO
     * @return 商品推荐
     */
    public Integer createProductRecommend(ProductRecommendCreateReqVO createVO) {
        CommonResult<Integer> createProductRecommendResult = productRecommendFeign.createProductRecommend(
                ProductRecommendConvert.INSTANCE.convert(createVO));
        createProductRecommendResult.checkError();
        return createProductRecommendResult.getData();
    }

    /**
     * 更新商品推荐
     *
     * @param updateVO 更新商品推荐 VO
     */
    public void updateProductRecommend(ProductRecommendUpdateReqVO updateVO) {
        CommonResult<Boolean> updateProductRecommendResult = productRecommendFeign.updateProductRecommend(
                ProductRecommendConvert.INSTANCE.convert(updateVO));
        updateProductRecommendResult.checkError();
    }

    /**
     * 删除商品推荐
     *
     * @param productRecommendId 商品推荐编号
     */
    public void deleteProductRecommend(Integer productRecommendId) {
        CommonResult<Boolean> deleteProductRecommendResult = productRecommendFeign.deleteProductRecommend(productRecommendId);
        deleteProductRecommendResult.checkError();
    }

    /**
     * 获得商品推荐分页
     *
     * @param pageVO 商品推荐分页查询
     * @return 商品推荐分页结果
     */
    public PageResult<ProductRecommendDetailVO> pageProductRecommend(ProductRecommendPageReqVO pageVO) {
        CommonResult<PageResult<ProductRecommendRespDTO>> pageProductRecommendResult = productRecommendFeign.pageProductRecommend(ProductRecommendConvert.INSTANCE.convert(pageVO));
        pageProductRecommendResult.checkError();
        // 拼接结果
        PageResult<ProductRecommendDetailVO> pageResult = ProductRecommendConvert.INSTANCE.convertPage(pageProductRecommendResult.getData());
        if (!CollectionUtils.isEmpty(pageResult.getList())) {
            // 获取商品信息，并进行拼接
            CommonResult<List<ProductSpuRespDTO>> listProductSpusResult = productSpuFeign.listProductSpus(
                    CollectionUtils.convertSet(pageResult.getList(), ProductRecommendDetailVO::getProductSpuId));
            listProductSpusResult.checkError();
            Map<Integer, ProductSpuRespDTO> productSpuMap = CollectionUtils.convertMap(listProductSpusResult.getData(), ProductSpuRespDTO::getId);
            pageResult.getList().forEach(detailVO ->
                    detailVO.setSpu(ProductRecommendConvert.INSTANCE.convert(productSpuMap.get(detailVO.getProductSpuId()))));
        }
        return pageResult;
    }

}
