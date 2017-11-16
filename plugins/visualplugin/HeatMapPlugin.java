package visualplugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.VisualPlugin;
import core.datapoint.DataSet;
import core.datapoint.Event;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import java.util.List;

/**
 * HeatMapPlugin - A data visualization plugin that shows the user a pseudo heat
 * map of the events gathered from framework. The Events are expected to but
 * need not contain population information. Events that contain such information
 * will appear on the map and a circle with a small radius will be shown if the
 * population is relatively low compared to other events, and a circle with a
 * larger radius will be shown if the population is relatively large. The
 * circles shown are color coded into pots with a blue color being lowest, and a
 * red color being the highest. If the population of an event is too low, it will not be marked 
 * on the map, though the subject of the event will appear.
 * 
 * @author APadilla
 *
 */
public class HeatMapPlugin implements VisualPlugin {
	private JFrame mapView;
	private static final int SIZE_CONST = 20000;

	private static final int PONE_THRESHOLD = 10000;
	private static final int PTWO_THRESHOLD = 20000;
	private static final int PTHREE_THRESHOLD = 30000;
	private static final int PFOUR_THRESHOLD = 40000;

	private static final int ALPHA = 100;
	private static final int PONE_R = 11;
	private static final int PONE_G = 41;
	private static final int PONE_B = 251;
	private static final int PTWO_R = 44;
	private static final int PTWO_G = 251;
	private static final int PTWO_B = 254;
	private static final int PTHREE_G = 253;
	private static final int PTHREE_B = 60;
	private static final int PFOUR_B = 55;
	private static final int PFIVE_R = 252;
	private static final int PFIVE_G = 23;
	private static final int PFIVE_B = 27;

	private Color potOneColor = new Color(PONE_R, PONE_G, PONE_B, ALPHA);
	private Color potTwoColor = new Color(PTWO_R, PTWO_G, PTWO_B, ALPHA);
	private Color potThreeColor = new Color(PONE_G, PTHREE_G, PTHREE_B, ALPHA);
	private Color potFourColor = new Color(PTHREE_G, PTHREE_G, PFOUR_B, ALPHA);
	private Color potFiveColor = new Color(PFIVE_R, PFIVE_G, PFIVE_B, ALPHA);

	/**
	 * MapView - makes use of the JMapViewer api to display a map as a jframe
	 * 
	 * @author APadilla
	 *
	 */
	private class MapView extends JFrame implements JMapViewerEventListener {
		private static final long serialVersionUID = 1L;
		private static final int WIDTH_HEIGHT = 400;
		private final JMapViewerTree treeMap;
		private final JLabel mperpLabelName;
		private final JLabel mperpLabelValue;
		private final JLabel zoomLabel;
		private final JLabel zoomValue;

		/**
		 * Instatiates a map view as a JFrame
		 */
		MapView() {
			super("JMapViewer");
			setSize(WIDTH_HEIGHT, WIDTH_HEIGHT);
			this.treeMap = new JMapViewerTree("Zones");

			map().addJMVListener(this);

			setLayout(new BorderLayout());
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			JPanel panel = new JPanel(new BorderLayout());
			JPanel panelTop = new JPanel();
			JPanel panelBottom = new JPanel();
			JPanel helpPanel = new JPanel();

			mperpLabelName = new JLabel("Meters/Pixels: ");
			mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));
			zoomLabel = new JLabel("Zoom: ");
			zoomValue = new JLabel(String.format("%s", map().getZoom()));

			add(panel, BorderLayout.NORTH);
			add(helpPanel, BorderLayout.SOUTH);
			panel.add(panelTop, BorderLayout.NORTH);
			panel.add(panelBottom, BorderLayout.SOUTH);
			JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
					+ "left double click or mouse wheel to zoom.");
			helpPanel.add(helpLabel);
			JButton button = new JButton("setDisplayToFitMapMarkers");

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map().setDisplayToFitMapMarkers();
				}
			});

			JComboBox<TileSource> tileSourceSelector = new JComboBox<>(
					new TileSource[] { new OsmTileSource.Mapnik() });

			tileSourceSelector.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					map().setTileSource((TileSource) e.getItem());
				}
			});

			JComboBox<TileLoader> tileLoaderSelector;
			tileLoaderSelector = new JComboBox<>(new TileLoader[] { new OsmTileLoader(map()) });
			tileLoaderSelector.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					map().setTileLoader((TileLoader) e.getItem());
				}
			});

			map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
			panelTop.add(tileSourceSelector);
			panelTop.add(tileLoaderSelector);
			final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
			showMapMarker.setSelected(map().getMapMarkersVisible());
			showMapMarker.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map().setMapMarkerVisible(showMapMarker.isSelected());
				}
			});
			panelBottom.add(showMapMarker);
			final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
			showTreeLayers.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					treeMap.setTreeVisible(showTreeLayers.isSelected());
				}
			});
			panelBottom.add(showTreeLayers);
			///
			final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
			showToolTip.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map().setToolTipText(null);
				}
			});
			panelBottom.add(showToolTip);

			final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
			showZoomControls.setSelected(map().getZoomControlsVisible());
			showZoomControls.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map().setZoomContolsVisible(showZoomControls.isSelected());
				}
			});
			panelBottom.add(showZoomControls);
			final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
			scrollWrapEnabled.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
				}
			});

			panelBottom.add(scrollWrapEnabled);
			panelBottom.add(button);

			panelTop.add(zoomLabel);
			panelTop.add(zoomValue);
			panelTop.add(mperpLabelName);
			panelTop.add(mperpLabelValue);

			add(treeMap, BorderLayout.CENTER);

			map().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						map().getAttribution().handleAttribution(e.getPoint(), true);
					}
				}
			});

			map().addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					Point p = e.getPoint();
					boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
					if (cursorHand) {
						map().setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
					if (showToolTip.isSelected())
						map().setToolTipText(map().getPosition(p).toString());
				}
			});
		}

		private JMapViewer map() {
			return treeMap.getViewer();
		}

		private void updateZoomParameters() {
			if (mperpLabelValue != null)
				mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
			if (zoomValue != null)
				zoomValue.setText(String.format("%s", map().getZoom()));
		}

		@Override
		public void processCommand(JMVCommandEvent command) {
			if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
					|| command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
				updateZoomParameters();
			}
		}
	}

	@Override
	public void initFigure() {
		this.mapView = new MapView();

	}

	@Override
	public void addData(DataSet set) {
		if (this.mapView == null) {
			return;
		}
		MapView treeMap = ((MapView) this.mapView);
		List<Event> events = set.getEvents();
		for (Event event : events) {
			double lon = event.getLongitude();
			double lat = event.getLatitude();
			String sub = event.getSubject();
			int pop = event.getQuantity();
			Color color = selectColor(pop);
			MapMarkerCircle circle = new MapMarkerCircle(null, sub, c(lat, lon), pop/SIZE_CONST);
			circle.setBackColor(color);
			circle.setColor(color.darker());

			treeMap.map().addMapMarker(circle);
		}
	}

	@Override
	public JFrame getFinalFigure() {
		this.mapView.setVisible(true);
		return this.mapView;
	}

	private static Coordinate c(double lat, double lon) {
		return new Coordinate(lat, lon);
	}

	private Color selectColor(int factor) {
		if (factor < PONE_THRESHOLD) {
			return potOneColor;
		}
		if (factor <= PONE_THRESHOLD && factor < PTWO_THRESHOLD) {
			return potTwoColor;
		}
		if (factor <= PTWO_THRESHOLD && factor < PTHREE_THRESHOLD) {
			return potThreeColor;
		}
		if (factor <= PTHREE_THRESHOLD && factor < PFOUR_THRESHOLD) {
			return potFourColor;
		} else {
			return potFiveColor;
		}
	}
	
	@Override 
	public String toString() {
		return "Heat Map Plugin";
	}

}
