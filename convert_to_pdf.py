#!/usr/bin/env python3
"""
HR Management Portal Documentation to PDF Converter
Creates a professional PDF from the Markdown documentation.
"""

import os
import sys
from pathlib import Path

# Try importing required libraries
try:
    import markdown
    from weasyprint import HTML, CSS
    from weasyprint.fonts import FontConfiguration
except ImportError as e:
    print("Missing required libraries. Please install them with:")
    print("pip install markdown weasyprint")
    print(f"Error: {e}")
    sys.exit(1)

def create_html_from_markdown(md_file_path, output_html_path):
    """Convert Markdown file to HTML with styling."""
    
    # Read the markdown file
    with open(md_file_path, 'r', encoding='utf-8') as f:
        markdown_content = f.read()
    
    # Configure markdown extensions
    md = markdown.Markdown(
        extensions=[
            'toc',
            'tables',
            'fenced_code',
            'codehilite',
            'attr_list',
            'def_list',
            'abbr',
            'footnotes'
        ],
        extension_configs={
            'toc': {
                'title': 'Table of Contents',
                'anchorlink': True,
                'permalink': True,
                'baselevel': 1
            },
            'codehilite': {
                'css_class': 'highlight',
                'use_pygments': True,
                'pygments_style': 'github'
            }
        }
    )
    
    # Convert markdown to HTML
    html_content = md.convert(markdown_content)
    
    # Create complete HTML document with professional styling
    full_html = f"""
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>HR Management Portal - Complete Documentation</title>
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap');
            
            * {{
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }}
            
            body {{
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                line-height: 1.6;
                color: #2d3748;
                font-size: 11pt;
                background: white;
            }}
            
            .container {{
                max-width: 210mm;
                margin: 0 auto;
                padding: 20px;
            }}
            
            /* Headers */
            h1 {{
                color: #1a365d;
                font-size: 28pt;
                font-weight: 700;
                margin: 30px 0 20px 0;
                padding-bottom: 10px;
                border-bottom: 3px solid #3182ce;
                page-break-before: always;
            }}
            
            h1:first-child {{
                page-break-before: auto;
                text-align: center;
                border-bottom: none;
                margin-top: 0;
                color: #2b6cb0;
            }}
            
            h2 {{
                color: #2c5aa0;
                font-size: 18pt;
                font-weight: 600;
                margin: 25px 0 15px 0;
                padding-bottom: 5px;
                border-bottom: 2px solid #e2e8f0;
            }}
            
            h3 {{
                color: #2d3748;
                font-size: 14pt;
                font-weight: 600;
                margin: 20px 0 12px 0;
            }}
            
            h4 {{
                color: #4a5568;
                font-size: 12pt;
                font-weight: 600;
                margin: 15px 0 10px 0;
            }}
            
            /* Paragraphs and text */
            p {{
                margin: 8px 0;
                text-align: justify;
            }}
            
            /* Lists */
            ul, ol {{
                margin: 10px 0;
                padding-left: 25px;
            }}
            
            li {{
                margin: 4px 0;
            }}
            
            /* Code blocks */
            pre {{
                background: #f7fafc;
                border: 1px solid #e2e8f0;
                border-radius: 6px;
                padding: 15px;
                margin: 15px 0;
                overflow-x: auto;
                font-family: 'JetBrains Mono', Monaco, 'Cascadia Code', monospace;
                font-size: 9pt;
                line-height: 1.4;
                page-break-inside: avoid;
            }}
            
            code {{
                background: #edf2f7;
                padding: 2px 6px;
                border-radius: 3px;
                font-family: 'JetBrains Mono', Monaco, 'Cascadia Code', monospace;
                font-size: 9pt;
                color: #e53e3e;
            }}
            
            pre code {{
                background: none;
                padding: 0;
                color: inherit;
            }}
            
            /* Tables */
            table {{
                width: 100%;
                border-collapse: collapse;
                margin: 15px 0;
                font-size: 10pt;
                page-break-inside: avoid;
            }}
            
            th, td {{
                border: 1px solid #e2e8f0;
                padding: 8px 12px;
                text-align: left;
            }}
            
            th {{
                background: #f7fafc;
                font-weight: 600;
                color: #2d3748;
            }}
            
            tr:nth-child(even) {{
                background: #f9f9f9;
            }}
            
            /* Blockquotes */
            blockquote {{
                border-left: 4px solid #3182ce;
                background: #f7fafc;
                padding: 15px 20px;
                margin: 15px 0;
                border-radius: 0 6px 6px 0;
                font-style: italic;
            }}
            
            /* Horizontal rules */
            hr {{
                border: none;
                height: 2px;
                background: linear-gradient(to right, #3182ce, transparent);
                margin: 30px 0;
            }}
            
            /* Links */
            a {{
                color: #3182ce;
                text-decoration: none;
            }}
            
            a:hover {{
                text-decoration: underline;
            }}
            
            /* Emphasis */
            strong {{
                font-weight: 600;
                color: #2d3748;
            }}
            
            em {{
                font-style: italic;
                color: #4a5568;
            }}
            
            /* Special boxes */
            .info-box {{
                background: #ebf8ff;
                border: 1px solid #90cdf4;
                border-radius: 6px;
                padding: 15px;
                margin: 15px 0;
            }}
            
            .warning-box {{
                background: #fefcbf;
                border: 1px solid #f6e05e;
                border-radius: 6px;
                padding: 15px;
                margin: 15px 0;
            }}
            
            /* Page breaks */
            .page-break {{
                page-break-before: always;
            }}
            
            /* Table of contents */
            .toc {{
                background: #f7fafc;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                padding: 20px;
                margin: 20px 0;
            }}
            
            .toc ul {{
                list-style-type: none;
                padding-left: 0;
            }}
            
            .toc li {{
                margin: 5px 0;
            }}
            
            .toc a {{
                color: #2c5aa0;
                text-decoration: none;
            }}
            
            /* Print optimizations */
            @media print {{
                body {{
                    font-size: 10pt;
                }}
                
                h1 {{
                    font-size: 24pt;
                }}
                
                h2 {{
                    font-size: 16pt;
                }}
                
                h3 {{
                    font-size: 12pt;
                }}
                
                pre {{
                    font-size: 8pt;
                }}
                
                table {{
                    font-size: 9pt;
                }}
            }}
            
            /* Syntax highlighting for code blocks */
            .highlight {{
                background: #f8f8f8;
            }}
            
            .highlight .c {{ color: #999988; font-style: italic }} /* Comment */
            .highlight .k {{ color: #000000; font-weight: bold }} /* Keyword */
            .highlight .s {{ color: #d14 }} /* String */
            .highlight .n {{ color: #333333 }} /* Name */
            .highlight .o {{ color: #000000; font-weight: bold }} /* Operator */
            .highlight .p {{ color: #333333 }} /* Punctuation */
        </style>
    </head>
    <body>
        <div class="container">
            {html_content}
        </div>
    </body>
    </html>
    """
    
    # Write HTML file
    with open(output_html_path, 'w', encoding='utf-8') as f:
        f.write(full_html)
    
    return output_html_path

def convert_html_to_pdf(html_file_path, output_pdf_path):
    """Convert HTML file to PDF with professional formatting."""
    
    # Custom CSS for PDF generation
    pdf_css = CSS(string="""
        @page {
            size: A4;
            margin: 2cm 1.5cm;
            
            @top-center {
                content: "HR Management Portal - System Documentation";
                font-family: Inter, sans-serif;
                font-size: 9pt;
                color: #666;
                border-bottom: 1px solid #ddd;
                padding-bottom: 5px;
                margin-bottom: 10px;
            }
            
            @bottom-center {
                content: "Page " counter(page) " of " counter(pages);
                font-family: Inter, sans-serif;
                font-size: 9pt;
                color: #666;
                border-top: 1px solid #ddd;
                padding-top: 5px;
                margin-top: 10px;
            }
        }
        
        /* First page special formatting */
        @page:first {
            @top-center {
                content: none;
            }
        }
        
        /* Better page breaks */
        h1, h2, h3 {
            page-break-after: avoid;
        }
        
        pre, table, blockquote {
            page-break-inside: avoid;
        }
        
        /* Orphans and widows */
        p {
            orphans: 3;
            widows: 3;
        }
    """)
    
    # Create font configuration for better font handling
    font_config = FontConfiguration()
    
    try:
        # Convert HTML to PDF
        html_doc = HTML(filename=html_file_path)
        html_doc.write_pdf(
            output_pdf_path,
            stylesheets=[pdf_css],
            font_config=font_config,
            optimize_size=('fonts', 'images')
        )
        return True
    except Exception as e:
        print(f"Error converting to PDF: {e}")
        return False

def main():
    """Main function to convert documentation to PDF."""
    
    # File paths
    current_dir = Path(__file__).parent
    md_file = current_dir / "HR_Management_Portal_Complete_Documentation.md"
    html_file = current_dir / "documentation_temp.html"
    pdf_file = current_dir / "HR_Management_Portal_Complete_Documentation.pdf"
    
    print("🚀 HR Management Portal - Documentation to PDF Converter")
    print("=" * 60)
    
    # Check if markdown file exists
    if not md_file.exists():
        print(f"❌ Error: Markdown file not found at {md_file}")
        return False
    
    print(f"📖 Reading markdown file: {md_file.name}")
    
    try:
        # Step 1: Convert Markdown to HTML
        print("🔄 Converting Markdown to HTML...")
        html_path = create_html_from_markdown(md_file, html_file)
        print("✅ HTML conversion completed")
        
        # Step 2: Convert HTML to PDF
        print("🔄 Converting HTML to PDF...")
        success = convert_html_to_pdf(html_path, pdf_file)
        
        if success:
            print("✅ PDF conversion completed successfully!")
            
            # Clean up temporary HTML file
            if html_file.exists():
                html_file.unlink()
            
            # Display results
            print("\n📄 Documentation Generated:")
            print(f"   📁 Location: {pdf_file}")
            print(f"   📊 Size: {pdf_file.stat().st_size / 1024 / 1024:.1f} MB")
            print(f"   🎯 Status: Ready for download")
            
            # Try to open the PDF (optional)
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
    
    finally:
        # Clean up temporary files
        if html_file.exists():
            html_file.unlink()

if __name__ == "__main__":
    success = main()
    
    if not success:
        print("\n🔧 Troubleshooting:")
        print("1. Make sure you have installed required packages:")
        print("   pip install markdown weasyprint")
        print("2. On Windows, you might need to install GTK for WeasyPrint:")
        print("   Download from: https://github.com/tschoonj/GTK-for-Windows-Runtime-Environment-Installer")
        print("3. Ensure the markdown file exists in the same directory")
        
    input("\nPress Enter to exit...")
