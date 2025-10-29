#!/usr/bin/env python3
"""
Simple PDF Generator for HR Management Portal Documentation
Uses reportlab to create a clean, professional PDF from the markdown content.
"""

import os
import sys
from pathlib import Path

# Try importing required libraries
try:
    from reportlab.lib.pagesizes import A4, letter
    from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle
    from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
    from reportlab.lib.units import inch, cm
    from reportlab.lib import colors
    from reportlab.lib.enums import TA_LEFT, TA_CENTER, TA_JUSTIFY
    import markdown
    from bs4 import BeautifulSoup
except ImportError as e:
    print("Missing required libraries. Please install them with:")
    print("pip install reportlab markdown beautifulsoup4")
    print(f"Error: {e}")
    sys.exit(1)

def clean_html_text(element):
    """Clean HTML element text by removing or fixing invalid attributes and tags."""
    if element is None:
        return ""
    
    # Get the text content, stripping HTML tags
    text = element.get_text(separator=' ', strip=True)
    
    # Remove any XML/HTML artifacts
    text = text.replace('<para>', '').replace('</para>', '')
    text = text.replace('<span>', '').replace('</span>', '')
    
    # Clean up any remaining angle brackets
    import re
    text = re.sub(r'<[^>]+>', '', text)
    
    return text.strip()

def parse_markdown_to_elements(md_content):
    """Convert markdown content to reportlab elements."""
    
    # Clean up the markdown content first to remove problematic HTML
    import re
    
    # Remove HTML tags that might be causing issues
    md_content = re.sub(r'<span[^>]*>[^<]*</span>', '', md_content)
    md_content = re.sub(r'<[^>]+>', '', md_content)
    
    # Convert markdown to HTML
    md = markdown.Markdown(extensions=['tables', 'fenced_code', 'toc'])
    html_content = md.convert(md_content)
    
    # Parse HTML with BeautifulSoup
    # Clean up HTML before parsing
    html_content = html_content.replace('className', 'class')
    
    # More aggressive cleaning
    html_content = re.sub(r'<para[^>]*>.*?</para>', '', html_content, flags=re.DOTALL)
    html_content = re.sub(r'<span[^>]*>.*?</span>', '', html_content, flags=re.DOTALL)
    
    soup = BeautifulSoup(html_content, 'html.parser')
    
    # Get styles
    styles = getSampleStyleSheet()
    
    # Create custom styles
    title_style = ParagraphStyle(
        'CustomTitle',
        parent=styles['Heading1'],
        fontSize=24,
        spaceAfter=30,
        textColor=colors.HexColor('#1a365d'),
        alignment=TA_CENTER
    )
    
    heading1_style = ParagraphStyle(
        'CustomHeading1',
        parent=styles['Heading1'],
        fontSize=18,
        spaceAfter=12,
        textColor=colors.HexColor('#2c5aa0'),
        spaceBefore=20
    )
    
    heading2_style = ParagraphStyle(
        'CustomHeading2',
        parent=styles['Heading2'],
        fontSize=14,
        spaceAfter=8,
        textColor=colors.HexColor('#2d3748'),
        spaceBefore=15
    )
    
    heading3_style = ParagraphStyle(
        'CustomHeading3',
        parent=styles['Heading3'],
        fontSize=12,
        spaceAfter=6,
        textColor=colors.HexColor('#4a5568'),
        spaceBefore=12
    )
    
    code_style = ParagraphStyle(
        'CodeStyle',
        parent=styles['Code'],
        fontSize=9,
        textColor=colors.HexColor('#e53e3e'),
        backColor=colors.HexColor('#f7fafc'),
        borderColor=colors.HexColor('#e2e8f0'),
        borderWidth=1,
        borderPadding=5
    )
    
    body_style = ParagraphStyle(
        'CustomBody',
        parent=styles['Normal'],
        fontSize=10,
        spaceAfter=6,
        alignment=TA_JUSTIFY,
        textColor=colors.HexColor('#2d3748')
    )
    
    elements = []
    first_h1 = True
    
    # Process each element in the parsed HTML
    for element in soup.find_all(['h1', 'h2', 'h3', 'h4', 'p', 'ul', 'ol', 'pre', 'code', 'table']):
        
        if element.name == 'h1':
            text = clean_html_text(element)
            if first_h1:
                # First h1 is the main title
                elements.append(Paragraph(text, title_style))
                first_h1 = False
            else:
                # Other h1s start new pages
                elements.append(PageBreak())
                elements.append(Paragraph(text, heading1_style))
            elements.append(Spacer(1, 0.2*inch))
            
        elif element.name == 'h2':
            elements.append(Paragraph(clean_html_text(element), heading2_style))
            elements.append(Spacer(1, 0.1*inch))
            
        elif element.name == 'h3':
            elements.append(Paragraph(clean_html_text(element), heading3_style))
            elements.append(Spacer(1, 0.08*inch))
            
        elif element.name == 'h4':
            elements.append(Paragraph(clean_html_text(element), heading3_style))
            elements.append(Spacer(1, 0.06*inch))
            
        elif element.name == 'p':
            text = clean_html_text(element)
            if text:
                try:
                    elements.append(Paragraph(text, body_style))
                    elements.append(Spacer(1, 0.05*inch))
                except Exception as e:
                    print(f"Warning: Skipping paragraph due to error: {e}")
                    print(f"Problematic text: {text[:100]}...")
                    continue
                
        elif element.name in ['ul', 'ol']:
            for li in element.find_all('li'):
                bullet = "• " if element.name == 'ul' else f"{li.parent.index(li) + 1}. "
                text = bullet + clean_html_text(li)
                elements.append(Paragraph(text, body_style))
                elements.append(Spacer(1, 0.03*inch))
            elements.append(Spacer(1, 0.05*inch))
            
        elif element.name == 'pre':
            code_text = element.get_text()
            # Split long code lines
            lines = code_text.split('\n')
            for line in lines:
                if len(line) > 100:
                    # Break long lines
                    while len(line) > 100:
                        break_point = line.rfind(' ', 0, 100)
                        if break_point == -1:
                            break_point = 100
                        elements.append(Paragraph(line[:break_point], code_style))
                        line = '  ' + line[break_point:].strip()
                    if line.strip():
                        elements.append(Paragraph(line, code_style))
                else:
                    elements.append(Paragraph(line if line.strip() else ' ', code_style))
            elements.append(Spacer(1, 0.1*inch))
            
        elif element.name == 'table':
            # Simple table handling
            rows = []
            for tr in element.find_all('tr'):
                row = [td.get_text().strip() for td in tr.find_all(['th', 'td'])]
                rows.append(row)
            
            if rows:
                table = Table(rows)
                table.setStyle(TableStyle([
                    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#f7fafc')),
                    ('TEXTCOLOR', (0, 0), (-1, 0), colors.black),
                    ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
                    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
                    ('FONTSIZE', (0, 0), (-1, 0), 10),
                    ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
                    ('BACKGROUND', (0, 1), (-1, -1), colors.white),
                    ('FONTSIZE', (0, 1), (-1, -1), 9),
                    ('GRID', (0, 0), (-1, -1), 1, colors.HexColor('#e2e8f0'))
                ]))
                elements.append(table)
                elements.append(Spacer(1, 0.1*inch))
    
    return elements

def create_pdf_from_markdown(md_file_path, output_pdf_path):
    """Create a PDF from markdown file using reportlab."""
    
    # Read the markdown file
    with open(md_file_path, 'r', encoding='utf-8') as f:
        markdown_content = f.read()
    
    # Create PDF document
    doc = SimpleDocTemplate(
        str(output_pdf_path),
        pagesize=A4,
        rightMargin=2*cm,
        leftMargin=2*cm,
        topMargin=2.5*cm,
        bottomMargin=2*cm
    )
    
    # Parse markdown and convert to reportlab elements
    elements = parse_markdown_to_elements(markdown_content)
    
    # Build PDF
    doc.build(elements)
    
    return True

def main():
    """Main function to convert documentation to PDF."""
    
    # File paths
    current_dir = Path(__file__).parent
    md_file = current_dir / "HR_Management_Portal_Complete_Documentation.md"
    pdf_file = current_dir / "HR_Management_Portal_Complete_Documentation.pdf"
    
    print("🚀 HR Management Portal - Simple PDF Generator")
    print("=" * 55)
    
    # Check if markdown file exists
    if not md_file.exists():
        print(f"❌ Error: Markdown file not found at {md_file}")
        return False
    
    print(f"📖 Reading markdown file: {md_file.name}")
    
    try:
        print("🔄 Converting Markdown to PDF...")
        success = create_pdf_from_markdown(md_file, pdf_file)
        
        if success:
            print("✅ PDF conversion completed successfully!")
            
            # Display results
            print("\n📄 Documentation Generated:")
            print(f"   📁 Location: {pdf_file}")
            print(f"   📊 Size: {pdf_file.stat().st_size / 1024 / 1024:.1f} MB")
            print(f"   🎯 Status: Ready for download")
            
            # Try to open the PDF
            try:
                if sys.platform.startswith('win'):
                    os.startfile(str(pdf_file))
                elif sys.platform.startswith('darwin'):
                    os.system(f'open "{pdf_file}"')
                else:
                    os.system(f'xdg-open "{pdf_file}"')
                print("📂 PDF opened successfully!")
            except:
                print("📂 PDF generated but could not auto-open. Please open manually.")
            
            return True
        else:
            print("❌ PDF conversion failed")
            return False
            
    except Exception as e:
        print(f"❌ Error during conversion: {e}")
        return False

if __name__ == "__main__":
    success = main()
    
    if not success:
        print("\n🔧 Troubleshooting:")
        print("1. Make sure you have installed required packages:")
        print("   pip install reportlab markdown beautifulsoup4")
        print("2. Ensure the markdown file exists in the same directory")
        
    input("\nPress Enter to exit...")
