package com.yjkmust.arcgisdemo.Service;

import com.esri.core.geodatabase.ShapefileFeature;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.query.QueryParameters;
import com.yjkmust.arcgisdemo.Bean.PipelineModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class PipelineService {

	private List<ShapefileFeatureTable> list;
	private ShapefileFeatureTable featureTable;

	public ShapefileFeatureTable getFeatureTable() {
		return featureTable;
	}

	public void setFeatureTable(ShapefileFeatureTable featureTable) {
		this.featureTable = featureTable;
	}

	public PipelineService(List<ShapefileFeatureTable> list) {
		this.list = list;
	}

	public PipelineService() {
	}

	public List<PipelineModel> getPointSearch(Geometry geo, SpatialReference sr, String sql) {
		List<PipelineModel> returnRes = new ArrayList<PipelineModel>();
		QueryParameters query = new QueryParameters();
		query.setGeometry(geo);
		query.setWhere(sql);
		query.setReturnGeometry(true);
		query.setOutFields(new String[] { "*" });
		query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
		query.setInSpatialReference(sr);
		Future<FeatureResult> resP = null;
		resP = featureTable.queryFeatures(query, null);
		try {
			for (Object res : resP.get()) {
				ShapefileFeature feature = (ShapefileFeature) res;
				PipelineModel model = new PipelineModel();
				model.setGeometry(feature.getGeometry().copy());
				model.setAttributes(feature.getAttributes());
				model.setId(feature.getId());
				model.setType(0);
				returnRes.add(model);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return returnRes;
	}


	public List<PipelineModel> getAllPipeTypes(String sql) {
		List<PipelineModel> returnRes = new ArrayList<PipelineModel>();
		QueryParameters query = new QueryParameters();
		query.setReturnGeometry(true);
		query.setWhere(sql);
		query.setOutFields(new String[] { "*" });
		query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
		for (int i = 0; i < list.size(); i++) {
			Future<FeatureResult> futureResult = list.get(i).queryFeatures(query, null);
			try {
				for (Object res : futureResult.get()) {
					ShapefileFeature feature = (ShapefileFeature) res;
					PipelineModel model = new PipelineModel();
					model.setGeometry(feature.getGeometry().copy());
					model.setAttributes(feature.getAttributes());
					model.setId(feature.getId());
					model.setType(0);
					returnRes.add(model);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return returnRes;
	}

	/**
	 * 查找shapeFile的属性
	 * 
	 * @param geo
	 * @param sr
	 * @param sql
	 * @return
	 */
	public List<PipelineModel> query(Geometry geo, SpatialReference sr, String sql) {
		List<PipelineModel> returnRes = new ArrayList<PipelineModel>();
		if (list == null) {
			return returnRes;
		}
		QueryParameters query = new QueryParameters();
		query.setGeometry(geo);
		query.setWhere(sql);
		query.setReturnGeometry(true);
		query.setOutFields(new String[] { "*" });
		query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
		query.setInSpatialReference(sr);
		Future<FeatureResult> resP = null;
		for (int i = 0; i < list.size(); i++) {
			resP = list.get(i).queryFeatures(query, null);
			try {
				for (Object res : resP.get()) {
					ShapefileFeature feature = (ShapefileFeature) res;
					PipelineModel model = new PipelineModel();
					model.setGeometry(feature.getGeometry().copy());
					model.setAttributes(feature.getAttributes());
					model.setId(feature.getId());
					model.setType(0);
					returnRes.add(model);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return returnRes;
	}
	public List<PipelineModel> query(Geometry geo, SpatialReference sr) {
		List<PipelineModel> returnRes = new ArrayList<PipelineModel>();
		if (list == null) {
			return returnRes;
		}
		QueryParameters query = new QueryParameters();
		query.setGeometry(geo);
		query.setReturnGeometry(true);
		query.setOutFields(new String[] { "*" });
		query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
		query.setInSpatialReference(sr);
		Future<FeatureResult> resP = null;
		for (int i = 0; i < list.size(); i++) {
			resP = list.get(i).queryFeatures(query, null);
			try {
				for (Object res : resP.get()) {
					ShapefileFeature feature = (ShapefileFeature) res;
					PipelineModel model = new PipelineModel();
					model.setGeometry(feature.getGeometry().copy());
					model.setAttributes(feature.getAttributes());
					model.setId(feature.getId());
					model.setType(0);
					returnRes.add(model);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return returnRes;
	}
	public List<PipelineModel> query(Geometry geo) {
		List<PipelineModel> returnRes = new ArrayList<PipelineModel>();
		if (list == null) {
			return returnRes;
		}
		QueryParameters query = new QueryParameters();
		query.setGeometry(geo);
		query.setReturnGeometry(true);
		query.setOutFields(new String[] { "*" });
		query.setSpatialRelationship(SpatialRelationship.INTERSECTS);
		Future<FeatureResult> resP = null;
		for (int i = 0; i < list.size(); i++) {
			resP = list.get(i).queryFeatures(query, null);
			try {
				for (Object res : resP.get()) {
					ShapefileFeature feature = (ShapefileFeature) res;
					PipelineModel model = new PipelineModel();
					model.setGeometry(feature.getGeometry().copy());
					model.setAttributes(feature.getAttributes());
					model.setId(feature.getId());
					model.setType(0);
					returnRes.add(model);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return returnRes;
	}
}
