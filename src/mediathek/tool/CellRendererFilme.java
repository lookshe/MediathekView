/*    
 *    MediathekView
 *    Copyright (C) 2008   W. Xaver
 *    W.Xaver[at]googlemail.com
 *    http://zdfmediathk.sourceforge.net/
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.tool;

import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import mediathek.controller.History;
import mediathek.controller.starter.Start;
import mediathek.daten.Daten;
import mediathek.daten.DatenDownload;
import mediathek.res.GetIcon;
import msearch.daten.DatenFilm;
import msearch.daten.ListeFilme;

public class CellRendererFilme extends DefaultTableCellRenderer {

    private static ImageIcon film_play_tab = null;
    private static ImageIcon film_play_sw_tab = null;
    private static ImageIcon film_rec_tab = null;
    private static ImageIcon film_rec_sw_tab = null;
    private Daten ddaten;
    private History history = null;

    public CellRendererFilme(Daten d) {
        ddaten = d;
        history = ddaten.history;
        film_play_tab = GetIcon.getIcon("film_play_tab.png");
        film_play_sw_tab = GetIcon.getIcon("film_play_sw_tab.png");
        film_rec_tab = GetIcon.getIcon("film_rec_tab.png");
        film_rec_sw_tab = GetIcon.getIcon("film_rec_sw_tab.png");
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        try {
            setBackground(null);
            setForeground(null);
            setFont(null);
            setIcon(null);
            setHorizontalAlignment(SwingConstants.LEADING);
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            int r = table.convertRowIndexToModel(row);
            int c = table.convertColumnIndexToModel(column);
            boolean start = false;
            DatenFilm datenFilm = (DatenFilm) table.getModel().getValueAt(r, DatenFilm.FILM_REF_NR);
            String url = datenFilm.arr[DatenFilm.FILM_URL_NR];
            boolean live = datenFilm.arr[DatenFilm.FILM_THEMA_NR].equals(ListeFilme.THEMA_LIVE);
            DatenDownload datenDownload = Daten.listeDownloadsButton.getDownloadUrlFilm(url);
            if (c == DatenFilm.FILM_NR_NR || c == DatenFilm.FILM_GROESSE_NR
                    || c == DatenFilm.FILM_DATUM_NR || c == DatenFilm.FILM_ZEIT_NR || c == DatenFilm.FILM_DAUER_NR) {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (c == DatenFilm.FILM_GROESSE_NR) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            if (c == DatenFilm.FILM_ABSPIELEN_NR) {
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setIcon(film_play_tab);
                } else {
                    setIcon(film_play_sw_tab);
                }
            } else if (c == DatenFilm.FILM_AUFZEICHNEN_NR) {
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setIcon(film_rec_tab);
                } else {
                    setIcon(film_rec_sw_tab);
                }
            }
            // Farben setzen
            if (datenDownload != null) {
                // gestartete Filme
                if (datenDownload.start != null) {
                    start = true;
                    setColor(this, datenDownload.start, isSelected);
                }
            }
            if (!start) {
                if (!live) {
                    // bei livestreams keine History anzeigen
                    if (datenFilm.neuerFilm) {
                        setForeground(GuiKonstanten.FARBE_FILM_NEU_FORGROUND);
                    } else if (history.contains(url)) {
                        if (!isSelected) {
                            setBackground(GuiKonstanten.FARBE_GRAU);
                        }
                    }
                } else {
                    setForeground(GuiKonstanten.DOWNLOAD_FARBE_LIVE);
                }
            }
            if (isSelected) {
                setFont(new java.awt.Font("Dialog", Font.BOLD, 12));
            } else {
                setFont(new java.awt.Font(null));
            }
        } catch (Exception ex) {
            Log.fehlerMeldung(630098552, Log.FEHLER_ART_PROG, this.getClass().getName(), ex);
        }
        return this;
    }

    private void setColor(Component c, Start s, boolean isSelected) {
        switch (s.status) {
            case Start.STATUS_INIT:
                if (isSelected) {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_WAIT_SEL);
                } else {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_WAIT);
                }
                break;
            case Start.STATUS_RUN:
                if (isSelected) {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_RUN_SEL);
                } else {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_RUN);
                }
                break;
            case Start.STATUS_FERTIG:
                if (isSelected) {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_FERTIG_SEL);
                } else {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_FERTIG);
                }
                break;
            case Start.STATUS_ERR:
                if (isSelected) {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_ERR_SEL);
                } else {
                    c.setBackground(GuiKonstanten.DOWNLOAD_FARBE_ERR);
                }
                break;
        }
    }
}
