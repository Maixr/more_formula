// 一键构建：准备依赖 → 生成 argfile → javac 编译 → 打包 jar。
//
// 为什么不写成 .cmd：Windows 下把 node 的 stdout 用 `>` 重定向会写成 UTF-16LE，
// javac 读 @argfile 时按平台编码解析就会看到乱码，报一堆「找不到包的符号」。
// 全部放在 Node 里做，直接以 UTF-8 无 BOM 写文件，从根上避开这个坑。
const fs = require('fs');
const path = require('path');
const { spawnSync, execFileSync } = require('child_process');
const { findTool } = require('./jdk');

const ROOT = path.resolve(__dirname, '..');
const GENARGS = path.join(__dirname, 'genargs.js');
const PACK = path.join(__dirname, 'pack.js');
const FETCH = path.join(__dirname, 'fetch-deps.js');
const JAVAC = findTool('javac');

// ---- 0) 确保 build/deps/ 就绪（缺什么补什么；已齐则秒过）----
// 放在构建里而不是让用户先手动跑：这几个 jar 不进仓库（build/ 被忽略），
// 别人 clone 下来直接 build 才不会撞上一堆「找不到符号」。
const fetch = spawnSync(process.execPath, [FETCH], { stdio: 'inherit' });
if (fetch.status !== 0) {
  console.error('[fetch-deps] 依赖准备失败，构建中止。');
  process.exit(1);
}

// ---- 1) 生成 argfile（UTF-8 无 BOM）----
const gen = spawnSync(process.execPath, [GENARGS], { encoding: 'utf8' });
if (gen.status !== 0) {
  console.error('[genargs] ' + (gen.stderr || gen.stdout || 'failed'));
  process.exit(1);
}
const argsPath = path.join(ROOT, 'build', 'args.txt');
fs.writeFileSync(argsPath, gen.stdout, 'utf8');

// ---- 2) 编译 ----
fs.rmSync(path.join(ROOT, 'build', 'classes'), { recursive: true, force: true });
const outPath = path.join(ROOT, 'build', 'out.txt');
const javac = spawnSync(JAVAC, ['@' + argsPath], { encoding: 'utf8' });
const log = (javac.stdout || '') + (javac.stderr || '');
fs.writeFileSync(outPath, log, 'utf8');

if (javac.status !== 0) {
  // 只把真正的 error 行打出来，避免刷屏
  const errors = log.split(/\r?\n/).filter((l) => /error:|错误:/.test(l));
  console.error(`[javac] FAILED (exit ${javac.status}), ${errors.length} error line(s):`);
  for (const l of errors.slice(0, 40)) console.error('  ' + l);
  process.exit(1);
}
console.log('[javac] OK');

// ---- 3) 打包 ----
execFileSync(process.execPath, [PACK], { stdio: 'inherit' });
