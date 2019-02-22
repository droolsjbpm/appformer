package org.dashbuilder.renderer.c3.client.charts.map.widgets;

import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.renderer.c3.client.charts.map.D3MapConf;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.CountriesGeoJsonService;
import org.dashbuilder.renderer.c3.client.jsbinding.d3.D3PathGenerator;
import org.dashbuilder.renderer.c3.client.jsbinding.d3.D3Projection;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Feature;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.D3;
import org.uberfire.client.views.pfly.widgets.D3.Selection;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;

@Templated
@Dependent
public class D3Map implements IsElement {

    /**
     * Colors schemes for the map can be taken from D3 scheme colors and when doing
     * it we can define which index of scheme we will be getting using the constant
     * D3_COLOR_SCHEME_TOTAL
     */
    private static final int D3_COLOR_SCHEME_TOTAL = 7;
    /**
     * In Marks map the max radius proportion based on the map width. For example, if 20, then the radius of markers can't be bigger than width / 20.
     */
    private static final int RADIUS_PROPORTION = 20;
    /**
     * The smallest radius for markers
     */
    private static final int MIN_RADIUS = 2;

    private int width;
    private int height;
    private D3MapConf conf;
    private D3.Scale colorScale;
    private CountriesGeoJsonService countriesGeoJsonService;
    private Map<String, Double> data;

    @Inject
    private MapTooltip mapTooltip;

    @Inject
    private HTMLDivElement mapContainer;

    D3 d3 = D3.Builder.get();
    String[] COLORS_SCHEME = d3.getSchemeGreens()[D3_COLOR_SCHEME_TOTAL];

    @PostConstruct
    public void init() {
        addTooltipElement();
    }

    @Override
    public HTMLElement getElement() {
        return mapContainer;
    }

    public void generateMap(int width, int height, D3MapConf conf) {
        mapContainer.innerHTML = "";
        this.width = width;
        this.height = height;
        this.conf = conf;
        this.countriesGeoJsonService = conf.getCountriesGeoJsonService();
        this.data = conf.getData();
        Element mapSVG = DomGlobal.document.createElementNS("http://www.w3.org/2000/svg", "svg");
        Selection d3Selection = d3.select(mapSVG);
        D3PathGenerator pathGenerator = createPathGenerator();

        IntSummaryStatistics statistics = data.values().stream().mapToInt(v -> v.intValue()).summaryStatistics();
        Integer[] domain = new Integer[] { statistics.getMin(), statistics.getMax() };
        colorScale = d3.scaleQuantize().domain(domain).range(COLORS_SCHEME);

        D3 map = createMap(d3Selection, pathGenerator);

        if (conf.isRegions()) {
            fillRegions(map);
        }
        if (conf.isMarkers()) {
            addMarkers(d3Selection, pathGenerator);
        }
        createLegend(d3Selection);
        mapContainer.appendChild(mapSVG);
    }

    private D3 createMap(Selection d3Selection, D3PathGenerator pathGenerator) {
        Feature[] countriesFeatures = countriesGeoJsonService.getCountries();
        return d3Selection.attr("width", width).attr("height", height).style("background", conf.getBackgroundColor())
                .append("g").attr("class", "countries").selectAll("path").data(countriesFeatures).enter().append("path")
                .attr("d", pathGenerator);
    }

    private void createLegend(D3 d3Selection) {
        int titleSize = 5;
        int legendSquareSize = 12;
        int legendSize = titleSize + (legendSquareSize * COLORS_SCHEME.length);
        AtomicInteger rectPos = new AtomicInteger();
        AtomicInteger textPos = new AtomicInteger(legendSquareSize);
        String translate = "translate(0, " + (height - legendSize - 2) + ")";
        D3 legendGroup = d3Selection.append("g").attr("transform", translate);

        legendGroup.append("text").attr("class", "map-legend-caption").attr("x", 0).attr("y", titleSize * -1)
                .text(conf.getTitle());

        D3 legendValuesGroup = legendGroup.append("g");

        legendValuesGroup.selectAll("rect").data(colorScale.range()).enter().append("rect")
                .attr("height", legendSquareSize).attr("width", legendSquareSize)
                .attr("y", (d, i, el) -> rectPos.getAndAdd(legendSquareSize)).attr("fill", (d, i, el) -> d)
                .append("svg:title").text((d, i, el) -> {
                    return String.join(" - ", getFormattedBoundaryValues((String) d));
                });
        legendValuesGroup.selectAll("text").data(colorScale.range()).enter().append("text")
                .attr("class", "map-legend-val").attr("x", legendSquareSize + 2)
                .attr("y", (d, i, el) -> textPos.getAndAdd(legendSquareSize)).text((d, i, el) -> {
                    String[] values = getFormattedBoundaryValues((String) d);
                    return "< " + values[1];
                });
    }

    private void fillRegions(D3 map) {
        Feature[] countriesFeatures = countriesGeoJsonService.getCountries();
        map.style("fill", (d, i, el) -> {
            Optional<Double> val = countriesGeoJsonService.valueByCountry(data, countriesFeatures[i]);
            d3.select(el[i]).attr("class", "fill-region");
            if (val.isPresent()) {
                return colorScale.call(colorScale, val.get());
            }
            return null;
        }).on("mouseenter", (d, i, el) -> {
            Optional<Double> val = countriesGeoJsonService.valueByCountry(data, countriesFeatures[i]);
            String countryName = countriesGeoJsonService.getCountryName(countriesFeatures[i]);
            mapTooltip.show(countryName, conf.getTitle(), val, conf.getFormatter());
            return null;
        }).on("mousemove", (d, i, el) -> {
            mapTooltip.move();
            return null;
        }).on("mouseleave", (d, i, el) -> {
            mapTooltip.hide();
            return null;
        }).on("click", (d, i, el) -> {
            countriesGeoJsonService.entryByCountry(data, countriesFeatures[i])
                    .ifPresent(v -> conf.getPathClickHandler().accept(v.getKey()));
            return null;
        });
    }

    private void addMarkers(Selection d3Selection, D3PathGenerator pathGenerator) {
        int maxRadius = width / RADIUS_PROPORTION;
        if (maxRadius < MIN_RADIUS) {
            maxRadius = MIN_RADIUS + 1;
        }
        D3.Scale radiusScale = d3.scaleSqrt().domain(colorScale.domain())
                .range(new Integer[] { MIN_RADIUS, maxRadius });
        Object[] countriesNames = data.keySet().toArray();
        d3Selection.append("g").selectAll("circle").data(countriesNames).enter().append("circle")
                .attr("class", "data-circle").style("fill", (d, i, el) -> {
                    double val = data.get(d);
                    return colorScale.call(colorScale, val);
                }).attr("r", (d, i, el) -> {
                    Optional<Feature> path = countriesGeoJsonService.countryByIdOrName((String) d);
                    if (path.isPresent()) {
                        double val = data.get(d);
                        return radiusScale.call(radiusScale, val);
                    }
                    return 0;
                }).attr("transform", (d, i, el) -> {
                    String translate = "";
                    Optional<Feature> path = countriesGeoJsonService.countryByIdOrName((String) d);
                    if (path.isPresent()) {
                        double[] center = pathGenerator.centroid(path.get());
                        translate = "translate(" + center[0] + ", " + center[1] + ")";
                    }
                    return translate;
                }).on("mouseenter", (d, i, el) -> {
                    Optional<Double> valOp = Optional.ofNullable(data.get(d));
                    mapTooltip.show((String) d, conf.getTitle(), valOp, conf.getFormatter());
                    return null;
                }).on("mousemove", (d, i, el) -> {
                    mapTooltip.move();
                    return null;
                }).on("mouseleave", (d, i, el) -> {
                    mapTooltip.hide();
                    return null;
                }).on("click", (d, i, el) -> {
                    conf.getPathClickHandler().accept((String) d);
                    return null;
                });
    }

    private D3PathGenerator createPathGenerator() {
        double w = (double) width;
        double h = (double) height;
        D3Projection projection = D3Projection.Builder.get().geoNaturalEarth2().scale(w / 5.5d)
                .translate(new double[] { w / 2d, h / 2d });
        D3PathGenerator pathGenerator = D3PathGenerator.Builder.get().geoPath().projection(projection);
        return pathGenerator;
    }

    private void addTooltipElement() {
        HTMLElement tooltipElement = mapTooltip.getElement();
        boolean elementNotAdded = DomGlobal.document.getElementById(tooltipElement.id) == null;
        if (elementNotAdded) {
            DomGlobal.document.body.appendChild(tooltipElement);
        }
    }

    private String[] getFormattedBoundaryValues(String color) {
        Object[] values = colorScale.invertExtent(color);
        String minStr = String.valueOf(values[1]);
        String maxStr = String.valueOf(values[1]);
        double max = Double.parseDouble(maxStr);
        double min = Double.parseDouble(minStr);
        return new String[] { conf.getFormatter().apply(min), conf.getFormatter().apply(max) };
    }
}