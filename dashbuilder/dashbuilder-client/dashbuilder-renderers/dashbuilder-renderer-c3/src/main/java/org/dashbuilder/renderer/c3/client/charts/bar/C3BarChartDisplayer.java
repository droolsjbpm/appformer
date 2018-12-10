package org.dashbuilder.renderer.c3.client.charts.bar;

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
import org.dashbuilder.renderer.c3.client.C3XYDisplayer;
import org.dashbuilder.renderer.c3.client.charts.area.C3AreaChartDisplayer;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;

@Dependent
public class C3BarChartDisplayer extends C3XYDisplayer<C3AreaChartDisplayer.View> {
    
    
    public interface View extends C3Displayer.View<C3BarChartDisplayer> {
    }
    
    private boolean rotated;
    private boolean stacked;
    private View view;
    
    
    @Inject
    public C3BarChartDisplayer(View view, FilterLabelSet filterLabelSet) {
        super(filterLabelSet);
        this.view = view;
        this.view.init(this);
    }
    
    public C3BarChartDisplayer notRotated() {
        this.setRotated(false);
        this.setStacked(false);
        return this;
    }
    
    public C3BarChartDisplayer rotated() {
        this.setRotated(true);
        this.setStacked(false);
        return this;
    }
    
    public C3BarChartDisplayer stacked() {
        this.setRotated(false);
        this.setStacked(true);
        return this;
    }
    
    public C3BarChartDisplayer stackedAndRotated() {
        this.setRotated(true);
        this.setStacked(true);
        return this;
    }
    
    @Override
    public DisplayerConstraints createDisplayerConstraints() {
        DataSetLookupConstraints lookupConstraints = new DataSetLookupConstraints()
                .setGroupRequired(true)
                .setGroupColumn(true)
                .setMaxColumns(10)
                .setMinColumns(2)
                .setExtraColumnsAllowed(true)
                .setExtraColumnsType( ColumnType.NUMBER)
                .setGroupsTitle(view.getGroupsTitle())
                .setColumnsTitle(view.getColumnsTitle())
                .setColumnTypes(new ColumnType[] {
                        ColumnType.LABEL,
                        ColumnType.NUMBER});

        return new DisplayerConstraints(lookupConstraints)
                .supportsAttribute(DisplayerAttributeDef.TYPE)
                .supportsAttribute(DisplayerAttributeDef.SUBTYPE)
                .supportsAttribute(DisplayerAttributeDef.RENDERER)
                .supportsAttribute(DisplayerAttributeGroupDef.COLUMNS_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.FILTER_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.REFRESH_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.GENERAL_GROUP)
                .supportsAttribute(DisplayerAttributeDef.CHART_WIDTH)
                .supportsAttribute(DisplayerAttributeDef.CHART_HEIGHT)
                .supportsAttribute(DisplayerAttributeDef.CHART_BGCOLOR)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_MARGIN_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.CHART_LEGEND_GROUP)
                .supportsAttribute(DisplayerAttributeGroupDef.AXIS_GROUP);
    }
    
    
    @Override
    protected String[][] createGroups() {
        String[][] groups = new String[0][0];
        if(isStacked()) {
            groups = new String[1][];
            groups[0] = dataSet.getColumns()
                                .stream().skip(1)
                                .map(DataColumn::getId)
                                .toArray(String[]::new);
            
        }
        return groups;
    }
    
    @Override
    protected C3AxisInfo createAxis() {
        C3AxisInfo axis = super.createAxis();
        axis.setRotated(isRotated());
        return axis;
    }

    public boolean isRotated() {
        return rotated;
    }
    
    public boolean isStacked() {
        return stacked;
    }

    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    @Override
    public View getView() {
        return view;
    }

}