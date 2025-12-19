c append sb c append sb 
k cc append sb k cc append sb 
newChunk 
f length files f f f length files f f f length files f f f length files f f 
font f getFont font f getFont font f getFont font f getFont 
c charExists getBaseFont font c charExists getBaseFont font c charExists getBaseFont font c charExists getBaseFont font 
cc k isSurrogatePair Utilities cc k isSurrogatePair Utilities 
c getType Character FORMAT Character c getType Character FORMAT Character c getType Character FORMAT Character c getType Character FORMAT Character 
currentFont font currentFont font currentFont font currentFont font 
cc k convertToUtf32 Utilities u cc k convertToUtf32 Utilities u 
length sb currentFont length sb currentFont length sb currentFont length sb currentFont 
f length files f f f length files f f 
newChunk toString sb currentFont Chunk newChunk toString sb currentFont Chunk newChunk toString sb currentFont Chunk newChunk toString sb currentFont Chunk 
font f getFont font f getFont 
setLength sb setLength sb setLength sb setLength sb 
u charExists getBaseFont font u charExists getBaseFont font 
u getType Character FORMAT Character u getType Character FORMAT Character 
currentFont font currentFont font currentFont font currentFont font 
currentFont font currentFont font 
length sb currentFont length sb currentFont 
newChunk toString sb currentFont Chunk newChunk toString sb currentFont Chunk 
setLength sb setLength sb 
Chunk processChar cc k sb StringBuffer DocumentException IOException 
newChunk Chunk 
k cc c 
c c 
c append sb 
font Font font Font 
c append sb c append sb c append sb c append sb 
currentFont font currentFont font 
