package util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.PDFExporter;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class CustomPDFExporter extends PDFExporter {
	@Override
	protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
	    for(UIColumn col : table.getColumns()) {
	        if(!col.isRendered()) {
	            continue;
	        }

	        if(col instanceof DynamicColumn) {
	            ((DynamicColumn) col).applyModel();
	        }

	        if(col.isExportable()) {
	           addHeaderValue(pdfTable, col.getFacet(columnType.facet()), FontFactory.getFont(FontFactory.TIMES, "iso-8859-1", Font.DEFAULTSIZE, Font.BOLD));
	        }
	    }
	}
	
	protected void addHeaderValue(PdfPTable pdfTable, UIComponent component, Font font) {
	   String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

	   PdfPCell cell = new PdfPCell(new Paragraph(value, font));
	   cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	   pdfTable.addCell(cell);
	}
}
