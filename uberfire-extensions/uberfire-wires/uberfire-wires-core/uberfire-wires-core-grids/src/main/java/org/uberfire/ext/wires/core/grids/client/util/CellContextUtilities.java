/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.util;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class CellContextUtilities {

    public static GridBodyCellEditContext makeCellRenderContext(final GridWidget gridWidget,
                                                                final BaseGridRendererHelper.RenderingInformation ri,
                                                                final BaseGridRendererHelper.ColumnInformation ci,
                                                                final int uiRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();

        final double headerRowHeight = getHeaderRowHeight(ri, column);
        final double headerRowsHeight = (headerRowHeight * column.getHeaderMetaData().size());

        double rowsHeight = 0;
        for (int i = 0; i < uiRowIndex; i++) {
            rowsHeight += gridWidget.getModel().getRow(i).getHeight();
        }

        final double lastRowYMiddle = gridWidget.getModel().getRow(uiRowIndex).getHeight() / 2;

        final double cellX = getCellX(gridWidget, ci);
        final double cellY = getHeaderY(gridWidget, ri) + headerRowsHeight + rowsHeight + lastRowYMiddle;

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();

        final double clipMinX = getClipMinX(gridWidget, floatingBlockInformation);
        final double clipMinY = getClipMinY(gridWidget);

        final double blockCellWidth = column.getWidth();

        return new GridBodyCellEditContext(cellX,
                                           cellY,
                                           blockCellWidth,
                                           getHeaderRowHeight(ri, column),
                                           clipMinY,
                                           clipMinX,
                                           uiRowIndex,
                                           ci.getUiColumnIndex(),
                                           floatingBlockInformation.getColumns().contains(column),
                                           gridWidget.getViewport().getTransform(),
                                           renderer,
                                           Optional.empty());
    }

    public static GridBodyCellEditContext makeHeaderCellRenderContext(final GridWidget gridWidget,
                                                                      final BaseGridRendererHelper.RenderingInformation ri,
                                                                      final BaseGridRendererHelper.ColumnInformation ci,
                                                                      final int uiHeaderRowIndex) {
        return makeHeaderCellRenderContext(gridWidget,
                                           ri,
                                           ci,
                                           null,
                                           uiHeaderRowIndex);
    }

    public static GridBodyCellEditContext makeHeaderCellRenderContext(final GridWidget gridWidget,
                                                                      final BaseGridRendererHelper.RenderingInformation ri,
                                                                      final BaseGridRendererHelper.ColumnInformation ci,
                                                                      final Point2D rp,
                                                                      final int uiHeaderRowIndex) {
        final GridColumn<?> column = ci.getColumn();
        final GridRenderer renderer = gridWidget.getRenderer();

        final double headerRowHeight = getHeaderRowHeight(ri, column);

        final double cellX = getCellX(gridWidget, ci);
        final double cellY = getHeaderY(gridWidget, ri) + (headerRowHeight * uiHeaderRowIndex);

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();

        final double clipMinX = getClipMinX(gridWidget, floatingBlockInformation);
        final double clipMinY = getClipMinY(gridWidget);

        //Check and adjust for blocks of columns sharing equal HeaderMetaData
        double blockCellX = cellX;
        double blockCellWidth = column.getWidth();
        final List<GridColumn<?>> gridColumns = ri.getAllColumns();
        final GridColumn.HeaderMetaData clicked = column.getHeaderMetaData().get(uiHeaderRowIndex);

        //Walk backwards to block start
        if (ci.getUiColumnIndex() > 0) {
            int uiLeadColumnIndex = ci.getUiColumnIndex() - 1;
            GridColumn<?> lead = gridColumns.get(uiLeadColumnIndex);
            while (uiLeadColumnIndex >= 0 && isSameHeaderMetaData(clicked,
                                                                  lead.getHeaderMetaData(),
                                                                  uiHeaderRowIndex)) {
                blockCellX = blockCellX - lead.getWidth();
                blockCellWidth = blockCellWidth + lead.getWidth();
                if (--uiLeadColumnIndex >= 0) {
                    lead = gridColumns.get(uiLeadColumnIndex);
                }
            }
        }

        //Walk forwards to block end
        if (ci.getUiColumnIndex() < gridColumns.size() - 1) {
            int uiTailColumnIndex = ci.getUiColumnIndex() + 1;
            GridColumn<?> tail = gridColumns.get(uiTailColumnIndex);
            while (uiTailColumnIndex < gridColumns.size() && isSameHeaderMetaData(clicked,
                                                                                  tail.getHeaderMetaData(),
                                                                                  uiHeaderRowIndex)) {
                blockCellWidth = blockCellWidth + tail.getWidth();
                tail = gridColumns.get(uiTailColumnIndex);
                if (++uiTailColumnIndex < gridColumns.size()) {
                    tail = gridColumns.get(uiTailColumnIndex);
                }
            }
        }

        return new GridBodyCellEditContext(blockCellX,
                                           cellY,
                                           blockCellWidth,
                                           headerRowHeight,
                                           clipMinY,
                                           clipMinX,
                                           uiHeaderRowIndex,
                                           ci.getUiColumnIndex(),
                                           floatingBlockInformation.getColumns().contains(column),
                                           gridWidget.getViewport().getTransform(),
                                           renderer,
                                           Optional.ofNullable(rp));
    }

    public static void editSelectedCell(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();

        if (gridModel.getSelectedHeaderCells().size() > 0) {
            final GridData.SelectedCell selectedCell = gridModel.getSelectedHeaderCells().get(0);
            final int uiHeaderRowIndex = selectedCell.getRowIndex();
            final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                             selectedCell.getColumnIndex());

            final GridColumn<?> column = gridModel.getColumns().get(uiColumnIndex);
            final GridColumn.HeaderMetaData headerMetaData = column.getHeaderMetaData().get(uiHeaderRowIndex);

            final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
            final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
            final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
            final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);

            final GridBodyCellEditContext context = CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                                                     ri,
                                                                                                     ci,
                                                                                                     uiHeaderRowIndex);

            headerMetaData.edit(context);
        } else if (gridModel.getSelectedCells().size() > 0) {

            final GridData.SelectedCell origin = gridModel.getSelectedCellsOrigin();
            if (origin == null) {
                return;
            }
            gridWidget.startEditingCell(origin.getRowIndex(),
                                        ColumnIndexUtilities.findUiColumnIndex(gridModel.getColumns(),
                                                                               origin.getColumnIndex()));
        }
    }

    private static boolean isSameHeaderMetaData(final GridColumn.HeaderMetaData clickedHeaderMetaData,
                                                final List<GridColumn.HeaderMetaData> columnHeaderMetaData,
                                                final int uiHeaderRowIndex) {
        if (uiHeaderRowIndex > columnHeaderMetaData.size() - 1) {
            return false;
        }
        return clickedHeaderMetaData.equals(columnHeaderMetaData.get(uiHeaderRowIndex));
    }

    private static double getCellX(final GridWidget gridWidget,
                                   final BaseGridRendererHelper.ColumnInformation ci) {

        return gridWidget.getComputedLocation().getX() + ci.getOffsetX() + ci.getColumn().getWidth() / 2;
    }

    private static double getHeaderY(final GridWidget gridWidget,
                                     final BaseGridRendererHelper.RenderingInformation ri) {
        final Group header = gridWidget.getHeader();
        final double headerRowsYOffset = ri.getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        return gridWidget.getComputedLocation().getY() + headerMinY;
    }

    private static double getHeaderRowHeight(final BaseGridRendererHelper.RenderingInformation ri,
                                             final GridColumn<?> column) {
        return ri.getHeaderRowsHeight() / column.getHeaderMetaData().size();
    }

    private static double getClipMinX(final GridWidget gridWidget,
                                      final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation) {
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();
        return gridWidget.getComputedLocation().getX() + floatingX + floatingWidth;
    }

    private static double getClipMinY(final GridWidget gridWidget) {
        return gridWidget.getComputedLocation().getY();
    }
}
