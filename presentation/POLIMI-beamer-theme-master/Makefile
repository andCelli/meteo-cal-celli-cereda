TARGET=test

AUXFILES=beamerthemePOLIMI.sty

PDFLATEX=pdflatex -synctex=1

$(TARGET).pdf: $(TARGET).tex $(AUXFILES)
	$(PDFLATEX) $<
	$(PDFLATEX) $<


.PHONY: pack clean pdfclean

pack:
	tar czvf $(TARGET).tar.gz $(TARGET).tex $(AUXFILES) figures

clean:
	$(RM) $(TARGET).log $(TARGET).aux $(TARGET).out $(TARGET).snm $(TARGET).nav $(TARGET).toc *~

pdfclean: clean
	$(RM) $(TARGET).pdf
