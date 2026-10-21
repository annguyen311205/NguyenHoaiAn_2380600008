import 'dart:math';
import 'package:flutter/material.dart';

class QrDisplayWidget extends StatelessWidget {
  final String data;
  final double size;

  const QrDisplayWidget({
    super.key,
    required this.data,
    this.size = 220,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.08),
            blurRadius: 16,
            offset: const Offset(0, 4),
          ),
        ],
        border: Border.all(color: const Color(0xFF1E3A8A).withOpacity(0.2), width: 2),
      ),
      child: CustomPaint(
        painter: _QrMatrixPainter(data: data),
        size: Size(size, size),
      ),
    );
  }
}

class _QrMatrixPainter extends CustomPainter {
  final String data;
  _QrMatrixPainter({required this.data});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = const Color(0xFF1E3A8A)
      ..style = PaintingStyle.fill;

    const int matrixSize = 25;
    final cellSize = size.width / matrixSize;

    // Deterministic random pattern derived from data hash
    final int hash = data.hashCode;
    final Random rng = Random(hash.abs());

    // 1. Draw 3 corner finder patterns
    _drawFinderPattern(canvas, 0, 0, cellSize, paint);
    _drawFinderPattern(canvas, (matrixSize - 7) * cellSize, 0, cellSize, paint);
    _drawFinderPattern(canvas, 0, (matrixSize - 7) * cellSize, cellSize, paint);

    // 2. Draw interior data modules
    for (int r = 0; r < matrixSize; r++) {
      for (int c = 0; c < matrixSize; c++) {
        // Skip finder pattern zones (7x7 corners)
        if (r < 7 && c < 7) continue;
        if (r < 7 && c >= matrixSize - 7) continue;
        if (r >= matrixSize - 7 && c < 7) continue;

        // Skip alignment zone
        if (r >= 16 && r <= 20 && c >= 16 && c <= 20) {
          if (r == 16 || r == 20 || c == 16 || c == 20 || (r == 18 && c == 18)) {
            canvas.drawRRect(
              RRect.fromRectAndRadius(
                Rect.fromLTWH(c * cellSize, r * cellSize, cellSize * 0.9, cellSize * 0.9),
                const Radius.circular(2),
              ),
              paint,
            );
          }
          continue;
        }

        // Timing patterns
        if (r == 6 || c == 6) {
          if ((r + c) % 2 == 0) {
            canvas.drawRect(
              Rect.fromLTWH(c * cellSize, r * cellSize, cellSize * 0.9, cellSize * 0.9),
              paint,
            );
          }
          continue;
        }

        // Deterministic bit fill
        if (rng.nextBool()) {
          canvas.drawRRect(
            RRect.fromRectAndRadius(
              Rect.fromLTWH(c * cellSize, r * cellSize, cellSize * 0.85, cellSize * 0.85),
              const Radius.circular(1.5),
            ),
            paint,
          );
        }
      }
    }
  }

  void _drawFinderPattern(Canvas canvas, double x, double y, double cellSize, Paint paint) {
    // Outer 7x7
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Rect.fromLTWH(x, y, cellSize * 7, cellSize * 7),
        const Radius.circular(6),
      ),
      paint,
    );
    // Inner white 5x5
    final whitePaint = Paint()
      ..color = Colors.white
      ..style = PaintingStyle.fill;
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Rect.fromLTWH(x + cellSize, y + cellSize, cellSize * 5, cellSize * 5),
        const Radius.circular(4),
      ),
      whitePaint,
    );
    // Center 3x3
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Rect.fromLTWH(x + cellSize * 2, y + cellSize * 2, cellSize * 3, cellSize * 3),
        const Radius.circular(3),
      ),
      paint,
    );
  }

  @override
  bool shouldRepaint(covariant _QrMatrixPainter oldDelegate) {
    return oldDelegate.data != data;
  }
}
