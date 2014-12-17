package org.molgenis.animaldb.plugins.administration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPRow;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

public class LabelGenerator
{

	private Document document;
	private PdfPTable table;
	private Rectangle pgSize;
	private int nrOfColumns;
	private String outputTemplate;

	public void startDocument(File pdfFile, String outputTemplate) throws LabelGeneratorException
	{
		this.outputTemplate = outputTemplate;
		if (outputTemplate.equalsIgnoreCase("Dymo"))
		{
			// TODO make proper template engine:
			// the sizes used here are for the type 99014 labels (101mmx54mm)
			// Rectangle pgSize2 = new Rectangle(310, 380);
			this.pgSize = new Rectangle(com.itextpdf.text.Utilities.millimetersToPoints(54),
					com.itextpdf.text.Utilities.millimetersToPoints(101));
			this.nrOfColumns = 1;
			document = new Document(this.pgSize);
			float lrmargin = com.itextpdf.text.Utilities.millimetersToPoints(3); // 5
			float tbmargin = com.itextpdf.text.Utilities.millimetersToPoints(3); // 7
			document.setMargins(lrmargin, lrmargin, tbmargin, tbmargin);
		}
		else
		{
			this.pgSize = PageSize.A4;
			this.nrOfColumns = 2;
			document = new Document(this.pgSize);
		}

		try
		{
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
		document.open();
		table = new PdfPTable(nrOfColumns);
		if (!this.outputTemplate.equalsIgnoreCase("A4"))
		{
			table.setWidthPercentage(100);
		}

	}

	public void nextPage() throws LabelGeneratorException
	{
		try
		{
			document.newPage();
			table = new PdfPTable(nrOfColumns);
			if (!this.outputTemplate.equalsIgnoreCase("A4"))
			{
				table.setWidthPercentage(100);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
	}

	public void finishPage() throws LabelGeneratorException
	{
		try
		{
			document.add(table);

		}
		catch (DocumentException e)
		{
			e.printStackTrace();
			throw new LabelGeneratorException();
		}
	}

	public void finishDocument() throws LabelGeneratorException
	{
		document.close();
	}

	/**
	 * Add a two-column label to the document: first column contains the
	 * headers, second the values.
	 * 
	 * @param elementHeaderList
	 * @param elementList
	 */
	public void addLabelToDocument(List<String> elementHeaderList, List<String> elementList)
	{

		PdfPCell labelCell = new PdfPCell();
		PdfPTable elementTable = new PdfPTable(7);
		// elementTable.
		int elementCtr = 0;
		Font valueFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, new BaseColor(0, 0, 0)));
		Font headerFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 5, Font.NORMAL, new BaseColor(0, 0, 0)));
		for (String header : elementHeaderList)
		{

			PdfPCell headerCell = new PdfPCell();
			Chunk headerChunk = new Chunk(header);
			headerChunk.setFont(headerFont);
			headerCell.addElement(headerChunk);
			headerCell.setColspan(2);
			headerCell.setBorder(0);
			// headerCell.setBorderWidthRight(0);
			// headerCell.setBorderWidthLeft(0);
			headerCell.setPaddingTop(0);
			headerCell.setPaddingBottom(5);
			// headerCell.setFixedHeight(15);
			// headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (elementCtr % 2 != 0)
			{
				headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			}

			elementTable.addCell(headerCell);

			PdfPCell valueCell = new PdfPCell();
			valueCell.setColspan(6);
			String value = elementList.get(elementCtr);
			if (value == null)
			{
				value = "";
			}

			Chunk valueChunk = new Chunk("" + value);
			valueChunk.setFont(valueFont);
			valueCell.addElement(valueChunk);
			valueCell.setBorderWidthLeft(0);
			valueCell.setBorder(0);
			valueCell.setPaddingTop(0);
			valueCell.setPaddingBottom(5);
			// valueCell.setFixedHeight(15);
			// valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (elementCtr % 2 != 0)
			{
				valueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			}
			elementTable.addCell(valueCell);
			elementCtr++;
		}
		elementTable.setWidthPercentage(100);
		if (this.outputTemplate.equalsIgnoreCase("A4"))
		{
			labelCell.setPadding(20);
			labelCell.setBorderWidth(1);
		}
		else
		{
			labelCell.setPadding(0);
			labelCell.setBorderWidth(0);
		}
		labelCell.addElement(elementTable);
		table.addCell(labelCell);

	}

	/**
	 * Add a one-column label to the document with only the values.
	 * 
	 * @param elementHeaderList
	 * @param elementList
	 */
	public void addLabelToDocument(List<String> elementList)
	{

		PdfPCell labelCell = new PdfPCell();
		for (String line : elementList)
		{
			labelCell.addElement(new Paragraph(line, new Font(Font.FontFamily.HELVETICA, 6)));
		}
		labelCell.setPadding(1);
		labelCell.setBorderWidth(1);
		table.addCell(labelCell);
	}

	public int getNrOfColumns()
	{
		return nrOfColumns;
	}

	public void setNrOfColumns(int nrOfColumns)
	{
		this.nrOfColumns = nrOfColumns;
	}

}
