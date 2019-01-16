package org.dashbuilder.renderer.c3.client.charts.meter;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.DisplayerAttributeGroupDef;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.renderer.c3.client.C3Displayer;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Color;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Gauge;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Threshold;

@Dependent
public class C3MeterChartDisplayer extends C3Displayer<C3Displayer.View> {
    
    
    public interface View extends C3Displayer.View<C3MeterChartDisplayer> {

        String[] getColorPattern();
    }
  
    private View view;
    
    @Inject
    public C3MeterChartDisplayer(View view, FilterLabelSet filterLabelSet, C3JsTypesFactory factory) {
        super(filterLabelSet, factory);
        this.view = view;
        this.view.init(this);
    }
    
    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(false)
                .setGroupAllowed(true)
                .setGroupColumn(true)
                .setMaxColumns(2)
                .setMinColumns(1)
                .setExtraColumnsAllowed(false)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setFunctionRequired(true)
                .setColumnTypes(new ColumnType[] {ColumnType.NUMBER},
                                new ColumnType[] {ColumnType.LABEL, ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                   .supportsAttribute(DisplayerAttributeDef.TYPE)
                   .supportsAttribute(DisplayerAttributeDef.RENDERER)
                   .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                   .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                   .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                   .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP)
                   .supportsAttribute(DisplayerAttributeGroupDef.METER_GROUP);
    }
    
    @Override
    public View getView() {
        return view;
    }
    
    @Override
    protected C3ChartConf buildConfiguration() {
        C3ChartConf conf = super.buildConfiguration();
        C3Gauge gaugeConf = createGauge();
        conf.setGauge(gaugeConf);
        return conf;
    }
    
    @Override
    protected String[][] createSeries() {
        List<DataColumn> columns = dataSet.getColumns();
        String[][] output = new String[0][0];
        if (columns.size() == 1) {
            output = new String[1][];
            output[0] = extractSingleColumnValues(columns.get(0));
        } else {
            DataColumn groupsColumn = columns.get(0);
            DataColumn valuesColumn = columns.get(1);
            output = extractGroupingValues(groupsColumn, valuesColumn);
        }
        return output;
    }
    
    @Override
    protected C3Color createColor() {
        C3Color color = super.createColor();
        int[] thresholdValues = {
                (int) displayerSettings.getMeterWarning(),
                (int) displayerSettings.getMeterCritical(),
                (int) displayerSettings.getMeterEnd()
        };
        C3Threshold c3Threshold = factory.c3Threshold(thresholdValues);
        color.setPattern(getView().getColorPattern());
        color.setThreshold(c3Threshold);
        return color;
    }
    
    private C3Gauge createGauge() {
        int meterStart = (int) displayerSettings.getMeterStart();
        int meterEnd = (int) displayerSettings.getMeterEnd();
        return factory.c3Gauge(meterStart, meterEnd);
    }

    String[][] extractGroupingValues(DataColumn groupsColumn, DataColumn valuesColumn) {
        int n = groupsColumn.getValues().size();
        String[][] output = new String[n][2];
        for (int i = 0; i < n; i++) {
            Object group = groupsColumn.getValues().get(i);
            Object value = valuesColumn.getValues().get(i);
            String groupStr = "", valueStr = "";
            if (group != null) {
                groupStr = group.toString();
            } else {
                
            }
            if (value != null) {
                valueStr = value.toString();
            }
            output[i][0] = groupStr;
            output[i][1] = valueStr;
        }
        return output;
    }

    String[] extractSingleColumnValues(DataColumn dataColumn) {
        List<?> values = dataColumn.getValues();
        String[] data = new String[values.size() + 1];
        data[0] = dataColumn.getId();
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            String valueStr = "";
            if (value != null) {
                valueStr = values.get(i).toString();
            } 
            data[i + 1] = valueStr;
        }
        return data;
    }
    
    

}
